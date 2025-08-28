package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.AppointmentsDTO;
import com.example.pet_care_booking.dto.ExaminationDTO;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.modal.Appointments;
import com.example.pet_care_booking.modal.Examination;
import com.example.pet_care_booking.modal.User;
import com.example.pet_care_booking.modal.Veterinarians;
import com.example.pet_care_booking.modal.enums.Status;
import com.example.pet_care_booking.repository.AppointmentRepository;
import com.example.pet_care_booking.repository.ExaminationRepository;
import com.example.pet_care_booking.repository.UserRepository;
import com.example.pet_care_booking.repository.VeterinarianRepository;
import com.example.pet_care_booking.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
   private final AppointmentRepository appointmentRepository;
   private final UserRepository userRepository;
   private final VeterinarianRepository veterinarianRepository;
   private final ExaminationRepository examinationRepository;

   @Override
   public Page<AppointmentsDTO> getAppointments(String nameOwer, String phoneNumber, String email, String namePet, String nameVet, String status, int page, int size) {
      Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
      Page<Appointments> apps;

      if (nameOwer == null && phoneNumber == null && email == null && namePet == null && nameVet == null) {
         apps = appointmentRepository.findAll(pageable);
      } else {
         apps = appointmentRepository.searchAppointment(nameOwer, phoneNumber, email, namePet, nameVet, status, pageable);
      }

      return apps.map(app -> {
         List<ExaminationDTO> exDTO = app.getExamination().stream()
                .map(
                       ex -> ExaminationDTO.builder()
                              .id(ex.getId())
                              .name(ex.getName())
                              .price(ex.getPrice())
                              .description(ex.getDescription())
                              .createdAt(ex.getCreatedAt())
                              .build()
                ).toList();

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
                .status(app.getStatus())
                .appointmentDay(app.getAppointmentDay())
                .appointmentTime(app.getAppointmentTime())
                .veterinarian(app.getVeterinarian())
                .examination(exDTO)
                .createdAt(app.getCreatedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
      });
   }

   @Override
   public void updateAppointment(Long id, AppointmentsDTO appointmentsDTO) {
      Appointments app = appointmentRepository.findById(id)
             .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

      app.setStatus(appointmentsDTO.getStatus());
      appointmentRepository.save(app);
   }

   @Override
   public void deleteAppointment(Long id) {
      Appointments app = appointmentRepository.findById(id)
             .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

      appointmentRepository.delete(app);
   }

   @Override
   public AppointmentsDTO addAppointment(Long vetId, AppointmentsDTO dto, String userName, String sessionId) {

      Veterinarians vet = veterinarianRepository.findById(vetId)
             .orElseThrow(() -> new AppException(ErrorCode.VET_NOT_FOUND));

      Appointments app = null;
      if (userName != null && !"anonymousUser".equals(userName)) {
         User user = userRepository.findUserByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

         List<Examination> exList = new ArrayList<>();
         for(ExaminationDTO exDTO : dto.getExamination()){
            Examination ex = examinationRepository.findById(exDTO.getId())
                   .orElseThrow(() -> new AppException(ErrorCode.EXAMINATION_NOT_FOUND));

            exList.add(ex);
         }

         app = Appointments.builder()
                .user(user)
                .veterinarian(vet)
                .ownerName(dto.getOwnerName() != null ? dto.getOwnerName() : user.getUserName())
                .email(dto.getEmail() != null ? dto.getEmail() : user.getEmail())
                .phoneNumber(dto.getPhoneNumber() != null ? dto.getPhoneNumber() : user.getPhoneNumber())
                .petName(dto.getPetName())
                .petType(dto.getPetType())
                .petGender(dto.getPetGender())
                .age(dto.getAge())
                .status(Status.PENDING)
                .note(dto.getNote())
                .examination(exList)
                .appointmentDay(dto.getAppointmentDay())
                .appointmentTime(dto.getAppointmentTime())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

         appointmentRepository.save(app);
      }

      return getAppointment(app);
   }

   @Override
   public Page<AppointmentsDTO> getAppointmentClient(String userName, String sessionId, String status, int page,
                                                     int size) {
      return null;
   }

   @Override
   public AppointmentsDTO updateAppointmentByClient(AppointmentsDTO appointmentsDTO, Long orderId, String
          userName, String sessionId) {
      return null;
   }

   @Override
   public void cancelAppointment(Long orderId, String userName, String sessionId) {

   }

   private AppointmentsDTO getAppointment(Appointments app) {

      List<ExaminationDTO> exDTO = getExamination(app);

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
             .status(app.getStatus())
             .appointmentDay(app.getAppointmentDay())
             .appointmentTime(app.getAppointmentTime())
             .veterinarian(app.getVeterinarian())
             .examination(exDTO)
             .createdAt(app.getCreatedAt())
             .updatedAt(app.getUpdatedAt())
             .build();
   }

   private List<ExaminationDTO> getExamination(Appointments app) {
      return app.getExamination().stream()
             .map(ex -> ExaminationDTO.builder()
                    .id(ex.getId())
                    .name(ex.getName())
                    .price(ex.getPrice())
                    .description(ex.getDescription())
                    .createdAt(ex.getCreatedAt())
                    .build())
             .toList();
   }

}
