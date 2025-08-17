package com.example.pet_care_booking.dto.response.category;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoriesResponse {
   Long id;
   String nameCate;
   String imageUrl;
   String description;
   LocalDateTime createdAt;
   LocalDateTime updatedAt;
   Long cateId;
}
