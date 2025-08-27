package com.example.pet_care_booking.modal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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

   @OneToMany(mappedBy = "veterinarian",cascade = CascadeType.ALL, orphanRemoval = true)
   @JsonIgnore
   private List<Appointments> appointment;
}
