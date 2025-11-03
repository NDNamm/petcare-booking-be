package com.example.pet_care_booking.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class OrderDetailDTO {
   private Long id;
   private Long quantity;
   private BigDecimal price;
   private String productName;
   private Long productId;
   private BigDecimal totalPrice;
   private String size;
   private String urlProductImage;
   private boolean reviewed;
   private Long variantId;
}
