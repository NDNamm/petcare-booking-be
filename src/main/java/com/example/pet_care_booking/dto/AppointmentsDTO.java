package com.example.pet_care_booking.dto;

import com.example.pet_care_booking.modal.Veterinarians;
import com.example.pet_care_booking.modal.enums.PetGender;
import com.example.pet_care_booking.modal.enums.PetType;
import com.example.pet_care_booking.modal.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class AppointmentsDTO {
   private Long id;
   private String ownerName;
   private String phoneNumber;
   private String email;
   private String petName;
   private PetType petType;
   private int age;
   private PetGender petGender;
   private Status status;
   private String note;
   private Date appointmentDay;
   private Time appointmentTime;
   private LocalDateTime createdAt;
   private LocalDateTime updatedAt;
   private Veterinarians veterinarian;
   private UserDTO user;
   private String sessionId;
   private List<ExaminationDTO> examination;
}
