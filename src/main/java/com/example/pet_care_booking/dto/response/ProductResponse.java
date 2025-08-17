package com.example.pet_care_booking.dto.response;

import com.example.pet_care_booking.modal.Images;
import com.example.pet_care_booking.modal.enums.ProductStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
   Long id;
   String namePro;
   String imageUrl;
   BigDecimal price;
   String description;
   ProductStatus status;
   BigDecimal averageRating;
   LocalDateTime createdAt;
   LocalDateTime updatedAt;
   List<Images> images;
}
