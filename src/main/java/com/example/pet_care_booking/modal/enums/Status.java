package com.example.pet_care_booking.modal.enums;

import lombok.Getter;

@Getter
public enum Status {
   PENDING("Chờ xác nhận"),
   CONFIRMED("Đã xác nhận"),
   COMPLETED("Hoàn tất"),
   CANCELED("Đã hủy");

   private final String vietnameseLabel;

   Status(String vietnameseLabel) {
      this.vietnameseLabel = vietnameseLabel;
   }
}
