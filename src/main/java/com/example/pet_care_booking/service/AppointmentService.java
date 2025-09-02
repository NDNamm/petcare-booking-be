package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.AppointmentsDTO;
import org.springframework.data.domain.Page;

public interface AppointmentService {
   Page<AppointmentsDTO> getAppointments(String ownerName, String phoneNumber, String email, String petName, String vetName, String status , int page, int size);
   void updateAppointment(Long id,AppointmentsDTO appointmentsDTO);
   void deleteAppointment(Long id);

   //Cua User
   AppointmentsDTO addAppointment(Long vetId,AppointmentsDTO appointmentsDTO, String userName, String sessionId);
   Page<AppointmentsDTO> getAppointmentClient(String userName, String sessionId, String status, int page, int size);
   AppointmentsDTO updateAppointmentByClient(AppointmentsDTO appointmentsDTO, Long appointId, String userName, String sessionId);
   void cancelAppointment(Long appointId, String userName, String sessionId);
}
