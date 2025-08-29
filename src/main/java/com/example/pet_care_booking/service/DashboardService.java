package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.DashboardDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface DashboardService {
   List<DashboardDTO> getDailyRevenue(LocalDateTime from, LocalDateTime to);
   List<DashboardDTO> getMonthlyRevenue(int year);
   List<Object[]> getOrderStatusCount();
   List<DashboardDTO> getTopProduct();
}
