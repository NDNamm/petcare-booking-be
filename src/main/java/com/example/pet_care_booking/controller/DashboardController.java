package com.example.pet_care_booking.controller;

import com.example.pet_care_booking.dto.ApiResponse;
import com.example.pet_care_booking.dto.DashboardDTO;
import com.example.pet_care_booking.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/api/dashboard")
@RestController
@RequiredArgsConstructor
public class DashboardController {

   private final DashboardService dashboardService;

   @GetMapping("/revenue/daily")
   public ApiResponse<List<DashboardDTO>> getDailyRevenue(
          @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
          @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
   ) {
      ApiResponse<List<DashboardDTO>> apiResponse = new ApiResponse<>();
      LocalDateTime start = fromDate.atStartOfDay();
      LocalDateTime end = toDate.atTime(23, 59, 59);

      apiResponse.setData(dashboardService.getDailyRevenue(start, end));

      return apiResponse;
   }

   @GetMapping("/revenue/month")
   public ApiResponse<List<DashboardDTO>> getMonthlyRevenue(@RequestParam("year") int year) {
      ApiResponse<List<DashboardDTO>> apiResponse = new ApiResponse<>();
      apiResponse.setData(dashboardService.getMonthlyRevenue(year));
      return apiResponse;
   }

   @GetMapping("/orders/status-count")
   public ApiResponse<List<Object[]>> getOrderStatusCount() {
      ApiResponse<List<Object[]>> apiResponse = new ApiResponse<>();
      apiResponse.setData(dashboardService.getOrderStatusCount());
      return apiResponse;
   }

   @GetMapping("/product/top-selling")
   public ApiResponse<List<DashboardDTO>> getTopProduct() {
      ApiResponse<List<DashboardDTO>> apiResponse = new ApiResponse<>();
      apiResponse.setData(dashboardService.getTopProduct());
      return apiResponse;
   }
}
