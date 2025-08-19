package com.example.pet_care_booking.dto;

import com.example.pet_care_booking.modal.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriesDTO {
   private Long id;

   @NotBlank(message = "CATEGORY_NAME_INVALID")
   @Size(min = 2, message = "CATEGORY_NAME_INVALID")
   private String nameCate;

   private String imageUrl;

   private String description;
   private String createdAt;
   private String updatedAt;
   private List<Product> product;
}
