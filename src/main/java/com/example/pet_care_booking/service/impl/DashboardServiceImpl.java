package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.DashboardDTO;
import com.example.pet_care_booking.repository.DashboardRepository;
import com.example.pet_care_booking.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

   private final DashboardRepository dashboardRepository;

   @Override
   public List<DashboardDTO> getDailyRevenue(LocalDateTime from, LocalDateTime to) {
      return dashboardRepository.getRevenueByDay(from,to);
   }

   @Override
   public List<DashboardDTO> getMonthlyRevenue(int year) {
      return dashboardRepository.getRevenueByMonth(year);
   }

   @Override
   public List<Object[]> getOrderStatusCount() {
      return dashboardRepository.getRevenueByStatus();
   }

   @Override
   public List<DashboardDTO> getTopProduct() {
      return dashboardRepository.getTopProduct();
   }
}
