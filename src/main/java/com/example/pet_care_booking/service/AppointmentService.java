package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.AppointmentsDTO;
import com.example.pet_care_booking.dto.VeterinariansDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
    Page<AppointmentsDTO> getAppointments(String ownerName, String phoneNumber, String email, String petName, String vetName, String status, int page, int size, Long userId);
    Page<AppointmentsDTO> getAppointmentsByDoctor(String ownerName, String phoneNumber, String email, String petName, String vetName, String status, int page, int size, Long userId);

    void updateAppointment(Long id, AppointmentsDTO appointmentsDTO);

    void deleteAppointment(Long id);

    //Cua User
    AppointmentsDTO addAppointment(Long vetId, AppointmentsDTO appointmentsDTO, String userName, String sessionId);

    Page<AppointmentsDTO> getAppointmentClient(String userName, String sessionId, String status, int page, int size);

    AppointmentsDTO updateAppointmentByClient(AppointmentsDTO appointmentsDTO, Long appointId, String userName, String sessionId);

    void cancelAppointment(Long appointId, String userName, String sessionId);

    byte[] generateInvoice(Long id);

    List<VeterinariansDTO> checkFreeTime(LocalDateTime start);

    void cancelAppointment(Long id);

    List<AppointmentsDTO> getAppointmentByPhone(String phone);

}
