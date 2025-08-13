package com.example.pet_care_booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {

   @NotBlank(message = "USERNAME_INVALID")
   @Size(min = 5, max = 50, message = "USERNAME_INVALID")
   String userName;

   @NotBlank(message = "PHONE_INVALID")
   @Size(min = 10, max = 15, message = "PHONE_INVALID")
   String phoneNumber;

   @NotBlank(message = "EMAIL_EXISTED")
   @Size(min = 5, max = 100, message = "EMAIL_EXISTED")
   String email;

   @NotBlank(message = "PASSWORD_INVALID")
   @Size(min = 8, max = 20, message = "PASSWORD_INVALID")
   String password;

   @NotBlank(message = "PASSWORD_INVALID")
   @Size(min = 8, max = 20, message = "PASSWORD_INVALID")
   String confirmPassword;
}
