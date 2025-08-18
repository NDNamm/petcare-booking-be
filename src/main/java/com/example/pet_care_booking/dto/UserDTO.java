package com.example.pet_care_booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
   private Long id;

   @NotBlank(message = "EMAIL_EXISTED")
   @Size(min = 5, max = 100, message = "EMAIL_EXISTED")
   private String email;

   @NotBlank(message = "USERNAME_INVALID")
   @Size(min = 5, max = 50, message = "USERNAME_INVALID")
   private String userName;

   @NotBlank(message = "PHONE_INVALID")
   @Size(min = 10, max = 15, message = "PHONE_INVALID")
   private String phoneNumber;

   private LocalDate dob;

   @NotBlank(message = "PASSWORD_INVALID")
   @Size(min = 8, max = 20, message = "PASSWORD_INVALID")
   private String password;

   @NotBlank(message = "PASSWORD_INVALID")
   @Size(min = 8, max = 20, message = "PASSWORD_INVALID")
   private String confirmPassword;

   private LocalDateTime createdAt;
   private LocalDateTime updatedAt;
   private String roleName;
}
