package com.example.pet_care_booking.modal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name= "images")
public class Images {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "image_url")
   private String imageUrl;

   @Column(name = "public_id")
   private String publicId;

   @Column(name = "size")
   private Long size;

   @ManyToOne
   @JsonIgnore
   @JoinColumn(name = "product_id")
   private Product product;
}