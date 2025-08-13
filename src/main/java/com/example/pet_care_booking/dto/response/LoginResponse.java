package com.example.pet_care_booking.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginResponse {
   private String userName;
   private String email;
   private String accessToken;
   private String roleName;
   private String phoneNumber;
}
