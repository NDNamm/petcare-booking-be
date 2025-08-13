package com.example.pet_care_booking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long id;

   @Column(name = "user_name", nullable = false, unique = true)
   private String userName;

   @Column(name = "phone_number", nullable = false, unique = true)
   private String phoneNumber;

   @Column(name = "email", nullable = false, unique = true)
   private String email;

   @Column(name = "password", nullable = false)
   private String password;

   @Column(name = "created_at")
   private LocalDateTime createdAt;

   @Column(name = "updated_at")
   private LocalDateTime updatedAt;

   @ManyToOne
   @JsonIgnore
   @JoinColumn(name = "role_id")
   private RoleEntity role;
}
