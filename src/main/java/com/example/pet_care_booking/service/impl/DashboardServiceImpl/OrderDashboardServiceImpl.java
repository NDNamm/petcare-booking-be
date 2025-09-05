package com.example.pet_care_booking.service.impl.DashboardServiceImpl;

import com.example.pet_care_booking.dto.Dashboard.OrderDashboardDTO;
import com.example.pet_care_booking.repository.DashboardRepository.OrderDashboardRepository;
import com.example.pet_care_booking.service.DashboardService.OrderDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDashboardServiceImpl implements OrderDashboardService {

   private final OrderDashboardRepository orderDashboardRepository;

   @Override
   public List<OrderDashboardDTO> getDailyRevenue(LocalDateTime from, LocalDateTime to) {
      return orderDashboardRepository.getRevenueByDay(from,to);
   }

   @Override
   public List<OrderDashboardDTO> getMonthlyRevenue(int year) {
      return orderDashboardRepository.getRevenueByMonth(year);
   }

   @Override
   public List<Object[]> getOrderStatusCount() {
      return orderDashboardRepository.getRevenueByStatus();
   }

   @Override
   public List<OrderDashboardDTO> getTopProduct() {
      return orderDashboardRepository.getTopProduct();
   }

   @Override
   public BigDecimal getTotalRevenue() {
      return orderDashboardRepository.getTotalRevenue();
   }
}
