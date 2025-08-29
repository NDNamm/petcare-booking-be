package com.example.pet_care_booking.modal;

import com.example.pet_care_booking.modal.enums.ProductStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.aspectj.weaver.ast.Var;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "image_url")
   private String imageUrl;
   @Column()
   private String slug;
   @Column(name = "name", nullable = false, unique = true)
   private String namePro;

//   @Column(name = "price", precision = 10, scale = 2)
//   private BigDecimal price;
//
//   @Column(name = "sl", nullable = true)
//   private Long sl;

   @Column(name = "description")
   private String description;

   @Column(name = "status", nullable = false)
   @Enumerated(EnumType.STRING)
   private ProductStatus status;

   @Column(name = "created_at")
   private LocalDateTime createdAt;

   @Column(name = "updated_at")
   private LocalDateTime updatedAt;

   @Column(name = "average_rating")
   private Double averageRating;

   @ManyToOne
   @JsonIgnore
   @JoinColumn(name = "category_id")
   private Categories category;

   @OneToMany(mappedBy = "product")
   @JsonIgnore
   private List<OrderDetail> orderDetails;

   @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
   private List<Images> images;

   @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
   @JsonIgnore
   private List<CartItem> cartItems;
   //
//   @OneToMany(mappedBy = "product")
//   @JsonIgnore
//   private List<Rating> rating;
//
   @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<Variants> variants;
}
