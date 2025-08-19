package com.example.pet_care_booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDTO {
   private long id;
   private String homeAddress;
   private String city;
   private String district;
   private String commune;
   private long userId;
}
