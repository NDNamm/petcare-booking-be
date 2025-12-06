package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.AppointmentsDTO;
import com.example.pet_care_booking.dto.ExaminationDTO;
import com.example.pet_care_booking.dto.VeterinariansDTO;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.modal.*;
import com.example.pet_care_booking.modal.enums.AppointStatus;
import com.example.pet_care_booking.repository.AppointmentRepository;
import com.example.pet_care_booking.repository.ExaminationRepository;
import com.example.pet_care_booking.repository.UserRepository;
import com.example.pet_care_booking.repository.VeterinarianRepository;
import com.example.pet_care_booking.service.AppointmentService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final ExaminationRepository examinationRepository;

    @Override
    public Page<AppointmentsDTO> getAppointments(String ownerName, String phoneNumber, String email,
                                                 String petName, String vetName, String status,
                                                 int page, int size, Long userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.getRole().getName().equals("DOCTOR")) {
            Veterinarians veterinarians = veterinarianRepository.findByUser(user);
            Page<Appointments> pages = appointmentRepository.findAppointmentsByUser(ownerName, phoneNumber, email, petName, vetName, status, veterinarians.getId(), pageable);
            return pages.map(this::getAppointment);
        } else {
            Page<Appointments> apps = (ownerName == null && phoneNumber == null &&
                    email == null && petName == null && vetName == null && status == null)
                    ? appointmentRepository.findAll(pageable)
                    : appointmentRepository.searchAppointment(ownerName, phoneNumber, email, petName, vetName, status, pageable);

            return apps.map(this::getAppointment);
        }
    }

    @Override
    public Page<AppointmentsDTO> getAppointmentsByDoctor(String ownerName, String phoneNumber, String email, String petName, String vetName, String status, int page, int size, Long userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Veterinarians veterinarians = veterinarianRepository.findByUser(user);
        Page<Appointments> pages = appointmentRepository.findAppointmentsByUser(ownerName, phoneNumber, email, petName, vetName, status, veterinarians.getId(), pageable);
        return pages.map(this::getAppointment);
    }

    @Override
    @Transactional
    public void updateAppointment(Long id, AppointmentsDTO appointmentsDTO) {
        Appointments app = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));
        app.setAppointStatus(appointmentsDTO.getAppointStatus());
        app.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(app);
    }

    @Override
    @Transactional
    public void deleteAppointment(Long id) {
        Appointments app = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));
        app.getExamination().clear();
        appointmentRepository.delete(app);
    }

    @Override
    @Transactional
    public AppointmentsDTO addAppointment(Long vetId, AppointmentsDTO dto, String userName, String sessionId) {
        Veterinarians vet = veterinarianRepository.findById(vetId)
                .orElseThrow(() -> new AppException(ErrorCode.VET_NOT_FOUND));

        AtomicReference<BigDecimal> totalPrice = new AtomicReference<>(BigDecimal.ZERO);
        List<Examination> exList = buildExaminations(dto, totalPrice);

        Appointments.AppointmentsBuilder builder = Appointments.builder()
                .veterinarian(vet)
                .petName(dto.getPetName())
                .petType(dto.getPetType())
                .petGender(dto.getPetGender())
                .age(dto.getAge())
                .appointStatus(AppointStatus.PENDING)
                .note(dto.getNote())
                .totalPrice(totalPrice.get())
                .examination(exList)
                .startTime(dto.getStart())
                .endTime(dto.getStart().plusMinutes(30))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now());

        if (userName != null && !"anonymousUser".equals(userName)) {
            User user = userRepository.findUserByUserName(userName)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            builder.user(user)
                    .ownerName(getOrDefault(dto.getOwnerName(), user.getUserName()))
                    .email(getOrDefault(dto.getEmail(), user.getEmail()))
                    .phoneNumber(getOrDefault(dto.getPhoneNumber(), user.getPhoneNumber()));
        } else {
            builder.sessionId(sessionId)
                    .ownerName(dto.getOwnerName())
                    .email(dto.getEmail())
                    .phoneNumber(dto.getPhoneNumber());
        }

        Appointments app = appointmentRepository.save(builder.build());
        return getAppointment(app);
    }

    @Override
    public Page<AppointmentsDTO> getAppointmentClient(String userName, String sessionId, String status,
                                                      int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Appointments> app;

        if (userName != null && !"anonymousUser".equals(userName)) {
            userRepository.findUserByUserName(userName)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            app = appointmentRepository.searchAppointmentsByUser(userName, status, pageable);
        } else {
            app = appointmentRepository.searchAppointmentsBySessionId(sessionId, status, pageable);
        }

        return app.map(this::getAppointment);
    }

    @Override
    @Transactional
    public AppointmentsDTO updateAppointmentByClient(AppointmentsDTO dto, Long appointId,
                                                     String userName, String sessionId) {
        Appointments app = appointmentRepository.findById(appointId)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        if (app.getAppointStatus() != AppointStatus.PENDING) {
            throw new AppException(ErrorCode.APPOINTMENT_CANNOT_BE_MODIFIED);
        }
        validateAppointmentOwnership(app, userName, sessionId);

        Veterinarians vet = veterinarianRepository.findById(dto.getVeterinarian().getId())
                .orElseThrow(() -> new AppException(ErrorCode.VET_NOT_FOUND));

        AtomicReference<BigDecimal> totalPrice = new AtomicReference<>(BigDecimal.ZERO);
        List<Examination> exList = buildExaminations(dto, totalPrice);

        app.setVeterinarian(vet);
        app.setOwnerName(dto.getOwnerName());
        app.setEmail(dto.getEmail());
        app.setPhoneNumber(dto.getPhoneNumber());
        app.setPetName(dto.getPetName());
        app.setPetType(dto.getPetType());
        app.setPetGender(dto.getPetGender());
        app.setAge(dto.getAge());
        app.setExamination(exList);
        app.setNote(dto.getNote());
        app.setTotalPrice(totalPrice.get());
        app.setAppointStatus(AppointStatus.PENDING);
//        app.setAppointmentDay(dto.gets());
//        app.setAppointmentTime(dto.getAppointmentTime());
        app.setUpdatedAt(LocalDateTime.now());

        appointmentRepository.save(app);
        return getAppointment(app);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long appointId, String userName, String sessionId) {
        Appointments app = appointmentRepository.findById(appointId)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        if (app.getAppointStatus() != AppointStatus.PENDING) {
            throw new AppException(ErrorCode.APPOINTMENT_CANNOT_BE_MODIFIED);
        }

        validateAppointmentOwnership(app, userName, sessionId);
        app.setAppointStatus(AppointStatus.CANCELLED);
        app.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(app);
    }

    public static String removeAccent(String s) {
        // Chuyển về dạng NFD (decompose)
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        // Loại bỏ các ký tự dấu (diacritics)
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }


    @Override
    public byte[] generateInvoice(Long id) {
        Appointments app = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            BaseFont baseFontBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

            Font titleFont = new Font(baseFontBold, 24, Font.BOLD, new BaseColor(41, 128, 185));
            Font headerFont = new Font(baseFontBold, 14, Font.BOLD, new BaseColor(44, 62, 80));
            Font normalFont = new Font(baseFont, 11, Font.NORMAL, new BaseColor(52, 73, 94));
            Font boldFont = new Font(baseFontBold, 11, Font.BOLD, new BaseColor(52, 73, 94));
            Font smallFont = new Font(baseFont, 9, Font.NORMAL, new BaseColor(127, 140, 141));
            Font totalFont = new Font(baseFontBold, 16, Font.BOLD, new BaseColor(231, 76, 60));

            // ========== HEADER SECTION ==========
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 1});
            headerTable.setSpacingAfter(20f);

            // Logo/Company Info (Left)
            PdfPCell companyCell = new PdfPCell();
            companyCell.setBorder(Rectangle.NO_BORDER);
            companyCell.setPaddingBottom(10f);

            Paragraph companyName = new Paragraph("PET CARE NQA", new Font(baseFontBold, 20, Font.BOLD, new BaseColor(41, 128, 185)));
            Paragraph companyAddress = new Paragraph("Dong Anh Ha Noi", smallFont);
            Paragraph companyPhone = new Paragraph("Hotline: 0236 123 4567", smallFont);
            Paragraph companyEmail = new Paragraph("Email: contact@petcare.vn", smallFont);

            companyCell.addElement(companyName);
            companyCell.addElement(companyAddress);
            companyCell.addElement(companyPhone);
            companyCell.addElement(companyEmail);
            headerTable.addCell(companyCell);

            // Invoice Info (Right)
            PdfPCell invoiceInfoCell = new PdfPCell();
            invoiceInfoCell.setBorder(Rectangle.NO_BORDER);
            invoiceInfoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            invoiceInfoCell.setPaddingBottom(10f);

            Paragraph invoiceTitle = new Paragraph("HOA DON", titleFont);
            invoiceTitle.setAlignment(Element.ALIGN_RIGHT);
            Paragraph invoiceNo = new Paragraph("So: #INV-" + String.format("%06d", app.getId()), boldFont);
            invoiceNo.setAlignment(Element.ALIGN_RIGHT);
            Paragraph invoiceDate = new Paragraph("Ngay: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont);
            invoiceDate.setAlignment(Element.ALIGN_RIGHT);

            invoiceInfoCell.addElement(invoiceTitle);
            invoiceInfoCell.addElement(invoiceNo);
            invoiceInfoCell.addElement(invoiceDate);
            headerTable.addCell(invoiceInfoCell);

            document.add(headerTable);

            // ========== DIVIDER LINE ==========
            PdfPTable divider = new PdfPTable(1);
            divider.setWidthPercentage(100);
            PdfPCell dividerCell = new PdfPCell();
            dividerCell.setBorder(Rectangle.NO_BORDER);
            dividerCell.setBorderWidthBottom(2f);
            dividerCell.setBorderColorBottom(new BaseColor(41, 128, 185));
            dividerCell.setPaddingBottom(10f);
            dividerCell.setPhrase(new Phrase(" "));
            divider.addCell(dividerCell);
            divider.setSpacingAfter(20f);
            document.add(divider);

            // ========== CUSTOMER INFO SECTION ==========
            PdfPTable customerTable = new PdfPTable(2);
            customerTable.setWidthPercentage(100);
            customerTable.setWidths(new float[]{1, 1});
            customerTable.setSpacingAfter(25f);

            // Customer Info (Left)
            PdfPCell customerCell = new PdfPCell();
            customerCell.setBorder(Rectangle.NO_BORDER);
            customerCell.setBackgroundColor(new BaseColor(236, 240, 241));
            customerCell.setPadding(15f);
//            customerCell.setBorderRadius(8f);

            Paragraph customerTitle = new Paragraph("THONG TIN KHACH HANG", headerFont);
            customerTitle.setSpacingAfter(10f);
            customerCell.addElement(customerTitle);
            customerCell.addElement(new Paragraph("Ho ten: " + removeAccent(app.getOwnerName()), normalFont));
            customerCell.addElement(new Paragraph("So dien thoai: " + (app.getPhoneNumber() != null ? app.getPhoneNumber() : "N/A"), normalFont));
            customerCell.addElement(new Paragraph("Email: " + (app.getEmail() != null ? app.getEmail() : "N/A"), normalFont));
            headerTable.addCell(customerCell);
            customerTable.addCell(customerCell);

            // Pet Info (Right)
            PdfPCell petCell = new PdfPCell();
            petCell.setBorder(Rectangle.NO_BORDER);
            petCell.setBackgroundColor(new BaseColor(232, 245, 233));
            petCell.setPadding(15f);

            Paragraph petTitle = new Paragraph("THONG TIN THU CUNG", headerFont);
            petTitle.setSpacingAfter(10f);
            petCell.addElement(petTitle);
            petCell.addElement(new Paragraph("Ten: " + app.getPetName(), normalFont));
            petCell.addElement(new Paragraph("Loai: " + (app.getPetType() != null ? removeAccent(String.valueOf(app.getPetType())) : "N/A"), normalFont));
            petCell.addElement(new Paragraph("Ngay kham: " + (app.getCreatedAt() != null ?
                    app.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A"), normalFont));
            customerTable.addCell(petCell);

            document.add(customerTable);

            // ========== SERVICES TABLE ==========
            Paragraph servicesTitle = new Paragraph("CHI TIET DICH VU", headerFont);
            servicesTitle.setSpacingAfter(15f);
            document.add(servicesTitle);

            PdfPTable servicesTable = new PdfPTable(4);
            servicesTable.setWidthPercentage(100);
            servicesTable.setWidths(new float[]{0.5f, 3f, 1f, 1.5f});
            servicesTable.setSpacingAfter(20f);

            // Table Header
            BaseColor headerBgColor = new BaseColor(41, 128, 185);
            Font tableHeaderFont = new Font(baseFontBold, 11, Font.BOLD, BaseColor.WHITE);

            String[] headers = {"STT", "Ten dich vu", "So luong", "Thanh tien"};
            for (String header : headers) {
                PdfPCell headerCell = new PdfPCell(new Phrase(header, tableHeaderFont));
                headerCell.setBackgroundColor(headerBgColor);
                headerCell.setPadding(10f);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                servicesTable.addCell(headerCell);
            }

            // Table Body
            List<Examination> examinations = app.getExamination();
            int index = 1;
            double subtotal = 0;

            BaseColor rowColor1 = BaseColor.WHITE;
            BaseColor rowColor2 = new BaseColor(245, 247, 250);

            for (Examination exam : examinations) {
                BaseColor rowColor = (index % 2 == 0) ? rowColor2 : rowColor1;
                double price = exam.getPrice() != null ? exam.getPrice().doubleValue() : 0;
                subtotal += price;

                // STT
                PdfPCell sttCell = new PdfPCell(new Phrase(String.valueOf(index), normalFont));
                sttCell.setBackgroundColor(rowColor);
                sttCell.setPadding(10f);
                sttCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                servicesTable.addCell(sttCell);

                // Service Name
                PdfPCell nameCell = new PdfPCell(new Phrase(removeAccent(exam.getName()), normalFont));
                nameCell.setBackgroundColor(rowColor);
                nameCell.setPadding(10f);
                servicesTable.addCell(nameCell);

                // Quantity
                PdfPCell qtyCell = new PdfPCell(new Phrase("1", normalFont));
                qtyCell.setBackgroundColor(rowColor);
                qtyCell.setPadding(10f);
                qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                servicesTable.addCell(qtyCell);

                // Price
                PdfPCell priceCell = new PdfPCell(new Phrase(String.format("%,.0f VND", price), normalFont));
                priceCell.setBackgroundColor(rowColor);
                priceCell.setPadding(10f);
                priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                servicesTable.addCell(priceCell);

                index++;
            }

            document.add(servicesTable);

            // ========== TOTAL SECTION ==========
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(50);
            totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.setSpacingBefore(10f);

            // Subtotal Row
            PdfPCell subtotalLabel = new PdfPCell(new Phrase("Tam tinh:", boldFont));
            subtotalLabel.setBorder(Rectangle.NO_BORDER);
            subtotalLabel.setPadding(8f);
            subtotalLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
            totalTable.addCell(subtotalLabel);

            PdfPCell subtotalValue = new PdfPCell(new Phrase(String.format("%,.0f VND", subtotal), normalFont));
            subtotalValue.setBorder(Rectangle.NO_BORDER);
            subtotalValue.setPadding(8f);
            subtotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.addCell(subtotalValue);

            // Discount Row (if any)
//            PdfPCell discountLabel = new PdfPCell(new Phrase("Giam gia:", boldFont));
//            discountLabel.setBorder(Rectangle.NO_BORDER);
//            discountLabel.setPadding(8f);
//            discountLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
//            totalTable.addCell(discountLabel);

//            PdfPCell discountValue = new PdfPCell(new Phrase("0 VND", normalFont));
//            discountValue.setBorder(Rectangle.NO_BORDER);
//            discountValue.setPadding(8f);
//            discountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            totalTable.addCell(discountValue);

            // Divider
            PdfPCell totalDivider1 = new PdfPCell(new Phrase(" "));
            totalDivider1.setBorder(Rectangle.NO_BORDER);
            totalDivider1.setBorderWidthBottom(1f);
            totalDivider1.setBorderColorBottom(new BaseColor(189, 195, 199));
            totalTable.addCell(totalDivider1);

            PdfPCell totalDivider2 = new PdfPCell(new Phrase(" "));
            totalDivider2.setBorder(Rectangle.NO_BORDER);
            totalDivider2.setBorderWidthBottom(1f);
            totalDivider2.setBorderColorBottom(new BaseColor(189, 195, 199));
            totalTable.addCell(totalDivider2);

            // Total Row
            PdfPCell totalLabel = new PdfPCell(new Phrase("TONG CONG:", new Font(baseFontBold, 14, Font.BOLD, new BaseColor(44, 62, 80))));
            totalLabel.setBorder(Rectangle.NO_BORDER);
            totalLabel.setPadding(10f);
            totalLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
            totalTable.addCell(totalLabel);

            PdfPCell totalValue = new PdfPCell(new Phrase(String.format("%,.0f VND", app.getTotalPrice()), totalFont));
            totalValue.setBorder(Rectangle.NO_BORDER);
            totalValue.setPadding(10f);
            totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.addCell(totalValue);

            document.add(totalTable);

            // ========== FOOTER SECTION ==========
            Paragraph footer = new Paragraph();
            footer.setSpacingBefore(50f);
            footer.add(new Chunk("Cam on quy khach da su dung dich vu cua chung toi!\n", new Font(baseFontBold, 12, Font.ITALIC, new BaseColor(41, 128, 185))));
            footer.add(new Chunk("Moi thac mac xin lien he hotline: 0236 123 4567", smallFont));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            // Signature Area
            PdfPTable signatureTable = new PdfPTable(2);
            signatureTable.setWidthPercentage(100);
            signatureTable.setSpacingBefore(40f);

            PdfPCell customerSignCell = new PdfPCell();
            customerSignCell.setBorder(Rectangle.NO_BORDER);
            customerSignCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            Paragraph customerSign = new Paragraph("Khach hang\n(Ky, ghi ro ho ten)", normalFont);
            customerSign.setAlignment(Element.ALIGN_CENTER);
            customerSignCell.addElement(customerSign);
            signatureTable.addCell(customerSignCell);

            PdfPCell staffSignCell = new PdfPCell();
            staffSignCell.setBorder(Rectangle.NO_BORDER);
            staffSignCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            Paragraph staffSign = new Paragraph("Nhan vien\n(Ky, ghi ro ho ten)", normalFont);
            staffSign.setAlignment(Element.ALIGN_CENTER);
            staffSignCell.addElement(staffSign);
            signatureTable.addCell(staffSignCell);

            document.add(signatureTable);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    @Override
    public List<VeterinariansDTO> checkFreeTime(LocalDateTime start) {
        LocalDateTime end = start.plusMinutes(30);
        List<Appointments> busyAppointments = appointmentRepository.findAppointments(start, end, AppointStatus.CANCELLED);
        List<Veterinarians> allVets = veterinarianRepository.findAll();

        Set<Long> busyVetIds = busyAppointments.stream().map(it -> it.getVeterinarian().getId()).collect(Collectors.toSet());

        return allVets.stream().filter(ft -> !busyVetIds.contains(ft.getId()))
                .map(v -> VeterinariansDTO.builder()
                        .id(v.getId())
                        .name(v.getName())
                        .build())
                .toList();

    }

    @Override
    public void cancelAppointment(Long id) {
        Appointments appointments = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        appointments.setAppointStatus(AppointStatus.CANCELLED);
        appointmentRepository.save(appointments);
    }

    @Override
    public List<AppointmentsDTO> getAppointmentByPhone(String phone) {
        List<Appointments> appointments = appointmentRepository.findAppointmentsByPhoneNumber(phone);
        List<AppointmentsDTO> appointmentsDTO = new ArrayList<>();
        for (Appointments appointments1 : appointments) {
            AppointmentsDTO dto = new AppointmentsDTO();
            dto.setId(appointments1.getId());
            dto.setNote(appointments1.getNote());
            dto.setOwnerName(appointments1.getOwnerName());
            dto.setEmail(appointments1.getEmail());
            dto.setPhoneNumber(appointments1.getPhoneNumber());
            dto.setStart(appointments1.getStartTime());
            dto.setEnd(appointments1.getEndTime());
            dto.setAge(appointments1.getAge());
            dto.setPetGender(appointments1.getPetGender());
            dto.setExamination(getExamination(appointments1));
            dto.setTotalPrice(appointments1.getTotalPrice());
            dto.setAppointStatus(appointments1.getAppointStatus());
            dto.setVeterinarian(appointments1.getVeterinarian());
            appointmentsDTO.add(dto);
        }
        return appointmentsDTO;
    }


    //    private void validateStatus(AppointStatus current, AppointStatus target){
//        if(current == AppointStatus.PENDING){
//            if(target != AppointStatus.CONFIRMED && target != AppointStatus.CANCELLED){
//                throw new RuntimeException("PENDING chỉ được chuyển sang CONFIRMED hoặc CANCELLED");
//            }
//        }
//
//        if(current == AppointStatus.CONFIRMED){
//            if(target != AppointStatus.IN_QUEUE && target != AppointStatus.CANCELLED){
//                throw new RuntimeException("CHECKED_IN chỉ được chuyển sang IN_QUEUE hoặc CANCELLED")
//            }
//        }
//
//        if(current == AppointStatus.IN_QUEUE || current == AppointStatus.IN_PROGRESS || current == AppointStatus.COMPLETED){
//            throw
//
//        }
//    }
    private AppointmentsDTO getAppointment(Appointments app) {
        return AppointmentsDTO.builder()
                .id(app.getId())
                .ownerName(app.getOwnerName())
                .phoneNumber(app.getPhoneNumber())
                .email(app.getEmail())
                .petName(app.getPetName())
                .age(app.getAge())
                .petType(app.getPetType())
                .petGender(app.getPetGender())
                .note(app.getNote())
                .appointStatus(app.getAppointStatus())
                .start(app.getStartTime())
                .end(app.getEndTime())
//                .appointmentDay(app.getStartTime())
//                .appointmentTime(app.getEndTime())
                .veterinarian(app.getVeterinarian())
                .examination(getExamination(app))
                .totalPrice(app.getTotalPrice())
                .createdAt(app.getCreatedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
    }

    private List<Examination> buildExaminations(AppointmentsDTO dto, AtomicReference<BigDecimal> totalPrice) {
        List<Examination> exList = new ArrayList<>();
        totalPrice.set(BigDecimal.ZERO);
        for (ExaminationDTO exDTO : dto.getExamination()) {
            Examination ex = examinationRepository.findById(exDTO.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.EXAMINATION_NOT_FOUND));
            exList.add(ex);
            totalPrice.set(totalPrice.get().add(ex.getPrice()));
        }
        return exList;
    }

    private List<ExaminationDTO> getExamination(Appointments app) {
        return app.getExamination().stream()
                .map(this::mapToExaminationDTO)
                .toList();
    }

    private ExaminationDTO mapToExaminationDTO(Examination ex) {
        return ExaminationDTO.builder()
                .id(ex.getId())
                .name(ex.getName())
                .price(ex.getPrice())
                .description(ex.getDescription())
                .createdAt(ex.getCreatedAt())
                .build();
    }

    private String getOrDefault(String value, String fallback) {
        return (value != null && !value.isBlank()) ? value : fallback;
    }

    private void validateAppointmentOwnership(Appointments app, String userName, String sessionId) {
        if (userName != null && !"anonymousUser".equals(userName)) {
            User user = userRepository.findUserByUserName(userName)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            if (app.getUser() == null || !app.getUser().getId().equals(user.getId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        } else if (sessionId != null) {
            if (!sessionId.equals(app.getSessionId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        } else {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }
}
