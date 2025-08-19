package com.example.pet_care_booking.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
   private Long id;
   private String createdAt;
   private Long userId;
   List<CartItemDTO> cartItems;
}
