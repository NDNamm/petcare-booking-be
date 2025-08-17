package com.example.pet_care_booking.modal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
public class Categories {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long id;

   @Column(name = "name", nullable = false, unique = true)
   private String nameCate;

   @Column(name = "image_url")
   private String imageUrl;

   @Column(name = "description")
   private String description;

   @Column(name = "created_at")
   private LocalDateTime createdAt;

   @Column(name = "updated_at")
   private LocalDateTime updatedAt;

   @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
   @JsonIgnore
   private List<Product> product;

}

