package com.example.pet_care_booking.dto;

import com.example.pet_care_booking.modal.Product;
import com.example.pet_care_booking.modal.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RatingDTO {
   private Double ratingValue;
   private Long id;
   private String comment;
   private LocalDateTime createdAt;
   private LocalDateTime updatedAt;
   private Product product;
   private User user;
   private String userName;
   private String namePro;

}
