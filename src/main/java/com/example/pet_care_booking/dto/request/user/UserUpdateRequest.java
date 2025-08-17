package com.example.pet_care_booking.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
   @NotBlank(message = "USERNAME_INVALID")
   @Size(min = 5, max = 50, message = "USERNAME_INVALID")
   String userName ;

   @NotBlank(message = "PHONE_INVALID")
   @Size(min = 10, max = 15, message = "PHONE_INVALID")
   String phoneNumber;
}
