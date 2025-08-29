package com.example.pet_care_booking.dto;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {
   private Long id;
   private Long quantity;
   private BigDecimal price;
   private BigDecimal totalPrice;
   private String size;
   private Long productId;
   private ProductDTO product;
}
