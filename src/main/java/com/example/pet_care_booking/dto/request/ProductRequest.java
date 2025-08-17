package com.example.pet_care_booking.dto.request;

import com.example.pet_care_booking.modal.Images;
import com.example.pet_care_booking.modal.enums.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
   @NotBlank(message = "PRODUCT_NAME_INVALID")
   @Size(min = 2, message = "PRODUCT_NAME_INVALID")
   String namePro;

   String imageUrl;

   @NotBlank(message = "PRODUCT_PRICE_INVALID")
   BigDecimal price;
   String description;

   @NotBlank(message = "PRODUCT_STATUS_INVALID")
   ProductStatus status;
   BigDecimal averageRating;
   LocalDateTime createdAt;
   LocalDateTime updatedAt;
   List<Images> images;
}
