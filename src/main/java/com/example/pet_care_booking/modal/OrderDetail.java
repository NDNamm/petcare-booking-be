package com.example.pet_care_booking.modal;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_details")
public class OrderDetail {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "quantity", nullable = false)
   private Long quantity;

   @Column(name = "price", precision = 10, scale = 2, nullable = false)
   private BigDecimal price;

   @Column(name = "total_price")
   private BigDecimal totalPrice;

   @ManyToOne
   @JoinColumn(name = "order_id")
   private Order order;

   @ManyToOne
   @JoinColumn(name = "product_id")
   private Product product;

}
