package com.example.pet_care_booking.service.DashboardService;

import com.example.pet_care_booking.dto.Dashboard.AppointmentDashboardDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentDashboardService {
   List<AppointmentDashboardDTO> getDailyRevenue(LocalDateTime from, LocalDateTime to);
   List<AppointmentDashboardDTO> getMonthlyRevenue(int year);
   List<Object[]> getOrderStatusCount();
   List<AppointmentDashboardDTO> getTopExam();
   BigDecimal getTotalRevenue();
}
