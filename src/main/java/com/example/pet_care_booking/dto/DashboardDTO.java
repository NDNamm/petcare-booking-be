package com.example.pet_care_booking.dto;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class DashboardDTO {
   private LocalDate date;
   private int month;
   private BigDecimal revenue;
   private String status;
   private Long orderCount;
   private String productName;
   private Long totalPro;

   public DashboardDTO(java.sql.Date date, BigDecimal revenue, Long orderCount) {
      this.date = date.toLocalDate();
      this.revenue = revenue;
      this.orderCount = orderCount;
   }

   public DashboardDTO(String productName, Long totalPro) {
      this.productName = productName;
      this.totalPro = totalPro;
   }

   public DashboardDTO(int month, BigDecimal revenue, Long orderCount) {
      this.month = month;
      this.revenue = revenue;
      this.orderCount = orderCount;
   }
}

