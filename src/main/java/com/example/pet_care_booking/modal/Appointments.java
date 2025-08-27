package com.example.pet_care_booking.modal;

import com.example.pet_care_booking.modal.enums.PetGender;
import com.example.pet_care_booking.modal.enums.PetType;
import com.example.pet_care_booking.modal.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "appointments")
public class Appointments {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "name_ower", nullable = false)
   private String nameOwer;

   @Column(name = "phone_number", nullable = false)
   private String phoneNumber;

   @Column(name = "email")
   private String email;

   @Column(name = "pet_name", nullable = false)
   private String petName;

   @Column(name = "pet_type")
   private PetType petType;

   @Column(name = "pet_age")
   private int age;

   @Column(name = "pet_gender")
   private PetGender petGender;

   @Column(name = "status")
   @Enumerated(EnumType.STRING)
   private Status status;

   @Column(name = "note")
   private String note;

   @Column(name = "appointment_date")
   private Date appointmentDate;

   @Column(name = "created_at")
   private LocalDateTime createdAt;

   @Column(name = "updated_at")
   private LocalDateTime updatedAt;

   @ManyToOne
   @JsonIgnore
   @JoinColumn(name = "vet_id")
   private Veterinarians veterinarian;
}
