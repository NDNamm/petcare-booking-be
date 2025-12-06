package com.example.pet_care_booking.modal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "addresses")
public class Address {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "home_address", nullable = false)
   private String homeAddress;

   @Column(name = "city")
   private String city;

   @Column(name = "district")
   private String district;

   @Column(name = "commune")
   private String commune;

   @Column(name = "session_id")
   private String sessionId;

   @ManyToOne
   @JsonIgnore
   @JoinColumn(name = "user_id")
   private User user;

   @OneToOne
   @JoinColumn(name = "order_id")
   @JsonIgnore
   private Order order;
}
