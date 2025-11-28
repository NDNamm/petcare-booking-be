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
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
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
                                                 int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Appointments> apps = (ownerName == null && phoneNumber == null &&
                email == null && petName == null && vetName == null && status == null)
                ? appointmentRepository.findAll(pageable)
                : appointmentRepository.searchAppointment(ownerName, phoneNumber, email, petName, vetName, status, pageable);

        return apps.map(this::getAppointment);
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
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusMinutes(30))
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

    @Override
    public byte[] generateInvoice(Long id) {
        Appointments app = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, out);
            document.open();

            // Dùng font mặc định, chỉ hỗ trợ ký tự Latin (không dấu)
            BaseFont baseFont = BaseFont.createFont(
                    BaseFont.HELVETICA,
                    BaseFont.CP1252,
                    BaseFont.NOT_EMBEDDED
            );
            Font titleFont = new Font(baseFont, 18, Font.BOLD, BaseColor.BLUE);
            Font normalFont = new Font(baseFont, 12, Font.NORMAL, BaseColor.BLACK);

            Paragraph title = new Paragraph("HOA DON LICH KHAM #" + app.getId(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            document.add(new Paragraph("Khach hang: " + app.getOwnerName(), normalFont));
            document.add(new Paragraph("Thu cung: " + app.getPetName(), normalFont));
            document.add(new Paragraph("Dich vu: " +
                    app.getExamination().stream()
                            .map(Examination::getName)
                            .collect(Collectors.joining(", ")), normalFont));
            document.add(new Paragraph("Tong tien: " + String.format("%,.0f VND", app.getTotalPrice()), normalFont));

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    @Override
    public List<VeterinariansDTO> checkFreeTime(LocalDateTime start) {
        LocalDateTime end = start.plusMinutes(30);
        List<Appointments> busyAppointments = appointmentRepository.findConflictingAppointments(start, end);
        List<Veterinarians> allVets = veterinarianRepository.findAll();

        Set<Long> busyVetIds = busyAppointments.stream().map(it -> it.getVeterinarian().getId()).collect(Collectors.toSet());

        return allVets.stream().filter(ft -> !busyVetIds.contains(ft.getId()))
                .map(v -> VeterinariansDTO.builder()
                        .id(v.getId())
                        .name(v.getName())
                        .build())
                .toList();

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
