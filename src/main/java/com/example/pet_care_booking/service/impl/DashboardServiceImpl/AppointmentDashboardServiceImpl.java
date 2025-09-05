package com.example.pet_care_booking.service.impl.DashboardServiceImpl;

import com.example.pet_care_booking.dto.Dashboard.AppointmentDashboardDTO;
import com.example.pet_care_booking.repository.DashboardRepository.AppointmentDashboardRepository;
import com.example.pet_care_booking.service.DashboardService.AppointmentDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentDashboardServiceImpl implements AppointmentDashboardService {

   private final AppointmentDashboardRepository appointmentDashboardRepository;

   @Override
   public List<AppointmentDashboardDTO> getDailyRevenue(LocalDateTime from, LocalDateTime to) {
      return appointmentDashboardRepository.getRevenueByDay(from,to);
   }

   @Override
   public List<AppointmentDashboardDTO> getMonthlyRevenue(int year) {
      return appointmentDashboardRepository.getRevenueByMonth(year);
   }

   @Override
   public List<Object[]> getOrderStatusCount() {
      return appointmentDashboardRepository.getRevenueByStatus();
   }

   @Override
   public List<AppointmentDashboardDTO> getTopExam() {
      return appointmentDashboardRepository.getTopExamination();
   }

   @Override
   public BigDecimal getTotalRevenue() {
      return appointmentDashboardRepository.getTotalRevenue();
   }
}
