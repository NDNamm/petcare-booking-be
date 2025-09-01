package com.example.pet_care_booking.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ExaminationDTO {
   private Long id;
   private String name;
   private BigDecimal price;
   private String description;
   private LocalDateTime createdAt;
   private LocalDateTime updatedAt;
}
