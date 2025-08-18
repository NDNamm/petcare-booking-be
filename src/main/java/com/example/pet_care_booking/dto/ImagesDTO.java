package com.example.pet_care_booking.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImagesDTO {
   private Long id;
   private String imageUrl;
   private String publicId;
   private Long size;

}