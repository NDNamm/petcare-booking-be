package com.example.pet_care_booking.modal;

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
@Table(name = "roles")
public class Role {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long id;

   @Column(name = "name", nullable = false, unique = true)
   private String name;

   @OneToMany(mappedBy = "role")
   @JsonIgnore
   private List<User> user;

}
