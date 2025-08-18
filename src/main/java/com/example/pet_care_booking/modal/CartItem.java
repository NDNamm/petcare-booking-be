package com.example.pet_care_booking.modal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cart_items")
public class CartItem {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "quantity")
   private int quantity;

   @Column(name = "price")
   private BigDecimal price;

   @Column(name = "total_price")
   private BigDecimal totalPrice;

   @ManyToOne
   @JsonIgnore
   @JoinColumn(name = "product_id")
   private Product product;

   @ManyToOne
   @JsonIgnore
   @JoinColumn(name = "cart_id")
   private Cart cart;
}
