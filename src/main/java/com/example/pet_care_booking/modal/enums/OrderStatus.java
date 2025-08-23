package com.example.pet_care_booking.modal.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
   PENDING("Chờ xác nhận"),
   CONFIRMED("Đã xác nhận"),
   SHIPPING("Đang giao hàng"),
   COMPLETED("Hoàn tất"),
   CANCELED("Đã hủy");

   private final String vietnameseLabel;

   OrderStatus(String label) {
      this.vietnameseLabel = label;
   }

}
