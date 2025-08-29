package com.example.pet_care_booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VeterinariansDTO {
   private long id;
   private String name;
   private String email;
   private String phoneNumber;
   private LocalDateTime createdAt;
   private LocalDateTime updatedAt;
}
