package com.example.pet_care_booking.controller.DashboardController;

import com.example.pet_care_booking.dto.ApiResponse;
import com.example.pet_care_booking.dto.Dashboard.AppointmentDashboardDTO;
import com.example.pet_care_booking.service.DashboardService.AppointmentDashboardService;
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

@RequestMapping("/api/dashboardApp")
@RestController
@RequiredArgsConstructor
public class AppointmentDashboardController {

   private final AppointmentDashboardService appointmentDashboardService;

   @GetMapping("/revenue/daily")
   public ApiResponse<List<AppointmentDashboardDTO>> getDailyRevenue(
   ) {
      ApiResponse<List<AppointmentDashboardDTO>> apiResponse = new ApiResponse<>();
      LocalDateTime start = LocalDate.now().minusDays(10).atStartOfDay();
      LocalDateTime end = LocalDate.now().atTime(23, 59, 59);

      apiResponse.setData(appointmentDashboardService.getDailyRevenue(start, end));

      return apiResponse;
   }

   @GetMapping("/revenue/month")
   public ApiResponse<List<AppointmentDashboardDTO>> getMonthlyRevenue(@RequestParam("year") int year) {
      ApiResponse<List<AppointmentDashboardDTO>> apiResponse = new ApiResponse<>();
      apiResponse.setData(appointmentDashboardService.getMonthlyRevenue(year));
      return apiResponse;
   }

   @GetMapping("/appoint/status-count")
   public ApiResponse<List<Object[]>> getOrderStatusCount() {
      ApiResponse<List<Object[]>> apiResponse = new ApiResponse<>();
      apiResponse.setData(appointmentDashboardService.getOrderStatusCount());
      return apiResponse;
   }

   @GetMapping("/exam/top")
   public ApiResponse<List<AppointmentDashboardDTO>> getTopProduct() {
      ApiResponse<List<AppointmentDashboardDTO>> apiResponse = new ApiResponse<>();
      apiResponse.setData(appointmentDashboardService.getTopExam());
      return apiResponse;

   }

   @GetMapping("/appoint/totalRevenue")
   public ApiResponse<BigDecimal> getTotalRevenue() {
      ApiResponse<BigDecimal> apiResponse = new ApiResponse<>();
      apiResponse.setData(appointmentDashboardService.getTotalRevenue());
      return apiResponse;
   }
}
