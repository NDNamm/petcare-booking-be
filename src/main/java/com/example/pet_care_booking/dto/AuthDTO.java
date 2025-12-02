package com.example.pet_care_booking.dto;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class AuthDTO {

   private Long userId;
   private String userName;
   private String email;
   private String provide;
   private String phoneNumber;
   private String nameRole;
   private String token;
}
