package com.example.pet_care_booking.modal;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rating")
public class Rating {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "rating_value", nullable = false)
   private int ratingValue;

   @Column(name = "comment", nullable = false)
   private String comment;

   @Column(name = "created_at", updatable = false)
   private LocalDateTime createdAt;

   @Column(name = "updated_at", updatable = false)
   private LocalDateTime updateAt;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "product_id")
   private Product product;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "user_id")
   private User user;
}
