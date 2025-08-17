package com.example.pet_care_booking.dto.request.category;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoriesRequest {
   String nameCate;
   String description;
   String imageUrl;
   LocalDateTime createdAt;
   LocalDateTime updatedAt;
}
