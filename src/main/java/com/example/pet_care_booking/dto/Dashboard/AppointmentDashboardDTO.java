package com.example.pet_care_booking.dto.Dashboard;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.sql.Date;

@Getter
@Setter
public class AppointmentDashboardDTO {
   private LocalDate date;
   private int month;
   private BigDecimal revenue;
   private String status;
   private Long appointCount;
   private String examinationName;
   private Long totalExamination;

   public AppointmentDashboardDTO(Date date, BigDecimal revenue, Long appointCount) {
      this.date = date.toLocalDate();
      this.revenue = revenue;
      this.appointCount = appointCount;
   }

   public AppointmentDashboardDTO(String examinationName, Long totalExamination) {
      this.examinationName = examinationName;
      this.totalExamination = totalExamination;
   }

   public AppointmentDashboardDTO(int month, BigDecimal revenue, Long appointCount) {
      this.month = month;
      this.revenue = revenue;
      this.appointCount = appointCount;
   }
}
