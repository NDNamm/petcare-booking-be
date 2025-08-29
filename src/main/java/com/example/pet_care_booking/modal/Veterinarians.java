package com.example.pet_care_booking.modal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "veterinarians")
public class Veterinarians {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "name")
   private String name;

   @Column(name = "email")
   private String email;

   @Column(name = "phone_number")
   private String phoneNumber;

   @Column(name = "created_at")
   private LocalDateTime createdAt;

   @Column(name = "updated_at")
   private LocalDateTime updatedAt;

   @OneToMany(mappedBy = "veterinarian",cascade = CascadeType.ALL, orphanRemoval = true)
   @JsonIgnore
   private List<Appointments> appointment;
}
