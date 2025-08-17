package com.example.pet_care_booking.modal.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductStatus {
   AVAILABLE("Đang bán"),
   OUT_OF_STOCK("Hết hàng"),
   DISCONTINUED("Ngừng kinh doanh");

   private final String label;


}

