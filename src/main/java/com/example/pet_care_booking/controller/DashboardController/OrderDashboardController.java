package com.example.pet_care_booking.controller.DashboardController;

import com.example.pet_care_booking.dto.ApiResponse;
import com.example.pet_care_booking.dto.Dashboard.OrderDashboardDTO;
import com.example.pet_care_booking.service.DashboardService.OrderDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/api/dashboard")
@RestController
@RequiredArgsConstructor
public class OrderDashboardController {

   private final OrderDashboardService orderDashboardService;

   @GetMapping("/revenue/daily")
   public ApiResponse<List<OrderDashboardDTO>> getDailyRevenue(
   ) {
      ApiResponse<List<OrderDashboardDTO>> apiResponse = new ApiResponse<>();
      LocalDateTime start = LocalDate.now().minusDays(7).atStartOfDay();
      LocalDateTime end = LocalDate.now().atTime(23, 59, 59);

      apiResponse.setData(orderDashboardService.getDailyRevenue(start, end));

      return apiResponse;
   }

   @GetMapping("/revenue/month")
   public ApiResponse<List<OrderDashboardDTO>> getMonthlyRevenue(@RequestParam("year") int year) {
      ApiResponse<List<OrderDashboardDTO>> apiResponse = new ApiResponse<>();
      apiResponse.setData(orderDashboardService.getMonthlyRevenue(year));
      return apiResponse;
   }

   @GetMapping("/orders/status-count")
   public ApiResponse<List<Object[]>> getOrderStatusCount() {
      ApiResponse<List<Object[]>> apiResponse = new ApiResponse<>();
      apiResponse.setData(orderDashboardService.getOrderStatusCount());
      return apiResponse;
   }

   @GetMapping("/product/top-selling")
   public ApiResponse<List<OrderDashboardDTO>> getTopProduct() {
      ApiResponse<List<OrderDashboardDTO>> apiResponse = new ApiResponse<>();
      apiResponse.setData(orderDashboardService.getTopProduct());
      return apiResponse;
   }

   @GetMapping("/orders/totalRevenue")
   public ApiResponse<BigDecimal> getTotalRevenue() {
      ApiResponse<BigDecimal> apiResponse = new ApiResponse<>();
      apiResponse.setData(orderDashboardService.getTotalRevenue());
      return apiResponse;
   }
}
