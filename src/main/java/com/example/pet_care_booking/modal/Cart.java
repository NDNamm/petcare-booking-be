package com.example.pet_care_booking.modal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cart")
public class Cart {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long id;

   @Column(name = "created_at")
   private LocalDateTime createdAt;

   @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
   @JsonIgnore
   private List<CartItem> items = new ArrayList<>();


}
