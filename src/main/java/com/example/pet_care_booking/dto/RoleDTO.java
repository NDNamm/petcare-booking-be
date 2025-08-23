package com.example.pet_care_booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleDTO {
   private Long id;
   private String name;
}
