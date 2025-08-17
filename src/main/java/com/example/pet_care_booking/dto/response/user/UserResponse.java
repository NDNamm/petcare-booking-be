package com.example.pet_care_booking.dto.response.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
   Long id;
   String userName;
   String email;
   String phoneNumber;
   String password;
   LocalDateTime createdAt;
   LocalDateTime updatedAt;
   String nameRole;

}
