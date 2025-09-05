package com.example.pet_care_booking.service.DashboardService;

import com.example.pet_care_booking.dto.Dashboard.OrderDashboardDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderDashboardService {
   List<OrderDashboardDTO> getDailyRevenue(LocalDateTime from, LocalDateTime to);
   List<OrderDashboardDTO> getMonthlyRevenue(int year);
   List<Object[]> getOrderStatusCount();
   List<OrderDashboardDTO> getTopProduct();
   BigDecimal getTotalRevenue();
}
