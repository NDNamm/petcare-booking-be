package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.AppointmentsDTO;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.modal.Appointments;
import com.example.pet_care_booking.modal.enums.Status;
import com.example.pet_care_booking.repository.AppointmentRepository;
import com.example.pet_care_booking.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
   private final AppointmentRepository appointmentRepository;

   @Override
   public Page<AppointmentsDTO> getAppointments(String nameOwer, String phoneNumber, String email, String namePet, String nameVet, Status status, int page, int size) {
      Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
      Page<Appointments> apps;

      if (nameOwer == null && phoneNumber == null && email == null && namePet == null && nameVet == null) {
         apps = appointmentRepository.findAll(pageable);
      } else {
         apps = appointmentRepository.searchAppointment(nameOwer, phoneNumber, email, namePet, nameVet, status, pageable);
      }
      return apps.map(app -> AppointmentsDTO.builder()
             .id(app.getId())
             .nameOwer(app.getNameOwer())
             .phoneNumber(app.getPhoneNumber())
             .email(app.getEmail())
             .petName(app.getPetName())
             .age(app.getAge())
             .petType(app.getPetType())
             .petGender(app.getPetGender())
             .note(app.getNote())
             .status(app.getStatus())
             .appointmentDate(app.getAppointmentDate())
             .veterinarian(app.getVeterinarian())
             .createdAt(app.getCreatedAt())
             .updatedAt(app.getUpdatedAt())
             .build());
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
   public AppointmentsDTO addAppointment(AppointmentsDTO appointmentsDTO, String userName, String sessionId) {


      return null;
   }

   @Override
   public Page<AppointmentsDTO> getAppointmentClient(String userName, String sessionId, String status, int page, int size) {
      return null;
   }

   @Override
   public AppointmentsDTO updateAppointmentByClient(AppointmentsDTO appointmentsDTO, Long orderId, String userName, String sessionId) {
      return null;
   }

   @Override
   public void cancelAppointment(Long orderId, String userName, String sessionId) {

   }

   private AppointmentsDTO getAppointment(Appointments app) {
      return AppointmentsDTO.builder()
             .id(app.getId())
             .nameOwer(app.getNameOwer())
             .phoneNumber(app.getPhoneNumber())
             .email(app.getEmail())
             .petName(app.getPetName())
             .age(app.getAge())
             .petType(app.getPetType())
             .petGender(app.getPetGender())
             .note(app.getNote())
             .status(app.getStatus())
             .appointmentDate(app.getAppointmentDate())
             .veterinarian(app.getVeterinarian())
             .createdAt(app.getCreatedAt())
             .updatedAt(app.getUpdatedAt())
             .build();
   }
}
