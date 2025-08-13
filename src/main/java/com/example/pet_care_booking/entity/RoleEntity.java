package com.example.pet_care_booking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class RoleEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long id;

   @Column(name = "name", nullable = false, unique = true)
   private String name;

   @OneToMany(mappedBy = "role")
   @JsonIgnore
   private List<UserEntity> user;

}
