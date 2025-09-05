package com.example.pet_care_booking.dto.Dashboard;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class OrderDashboardDTO {
   private LocalDate date;
   private int month;
   private BigDecimal revenue;
   private String status;
   private Long orderCount;
   private String productName;
   private Long totalPro;

   public OrderDashboardDTO(java.sql.Date date, BigDecimal revenue, Long orderCount) {
      this.date = date.toLocalDate();
      this.revenue = revenue;
      this.orderCount = orderCount;
   }

   public OrderDashboardDTO(String productName, Long totalPro) {
      this.productName = productName;
      this.totalPro = totalPro;
   }

   public OrderDashboardDTO(int month, BigDecimal revenue, Long orderCount) {
      this.month = month;
      this.revenue = revenue;
      this.orderCount = orderCount;
   }
}

