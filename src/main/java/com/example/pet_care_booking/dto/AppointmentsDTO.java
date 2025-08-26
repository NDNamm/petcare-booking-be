package com.example.pet_care_booking.dto;

import com.example.pet_care_booking.modal.Veterinarians;
import com.example.pet_care_booking.modal.enums.PetGender;
import com.example.pet_care_booking.modal.enums.PetType;
import com.example.pet_care_booking.modal.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
public class AppointmentsDTO {
   private Long id;
   private String nameOwer;
   private String phoneNumber;
   private String email;
   private String petName;
   private PetType petType;
   private int age;
   private PetGender petGender;
   private Status status;
   private String note;
   private Date appointmentDate;
   private LocalDateTime createdAt;
   private LocalDateTime updatedAt;
   private Veterinarians veterinarian;
}
