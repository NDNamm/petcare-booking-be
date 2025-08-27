package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.AppointmentsDTO;
import com.example.pet_care_booking.modal.enums.Status;
import org.springframework.data.domain.Page;

public interface AppointmentService {
   Page<AppointmentsDTO> getAppointments(String nameOwer, String phoneNumber, String email, String namePet, String nameVet, Status status , int page, int size);
   void updateAppointment(Long id,AppointmentsDTO appointmentsDTO);
   void deleteAppointment(Long id);

   //Cua User
   AppointmentsDTO addAppointment(AppointmentsDTO appointmentsDTO, String userName, String sessionId);
   Page<AppointmentsDTO> getAppointmentClient(String userName, String sessionId, String status, int page, int size);
   AppointmentsDTO updateAppointmentByClient(AppointmentsDTO appointmentsDTO, Long orderId, String userName, String sessionId);
   void cancelAppointment(Long orderId, String userName, String sessionId);
}
