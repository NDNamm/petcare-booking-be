package com.example.pet_care_booking.modal.enums;

import lombok.Getter;

@Getter
public enum AppointStatus {
   PENDING("Chờ xác nhận"),
   CONFIRMED("Đã xác nhận"),
   COMPLETED("Hoàn tất"),
   CANCELED("Đã hủy");

   private final String vietnameseLabel;

   AppointStatus(String vietnameseLabel) {
      this.vietnameseLabel = vietnameseLabel;
   }
}
