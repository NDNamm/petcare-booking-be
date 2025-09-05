package com.example.pet_care_booking.modal;

import com.example.pet_care_booking.modal.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "session_id")
   private String sessionId;

   @Column(name = "name")
   private String name;

   @Column(name = "phone_number")
   private String phoneNumber;

   @Column(name = "total_amount", precision = 10, scale = 2)
   private BigDecimal totalAmount;

   @Column(name = "order_date")
   private LocalDateTime orderDate;

       @Column(name = "status")
       @Enumerated(EnumType.STRING)

       private OrderStatus status;

   @Column(name = "note")
   private String note;

   @JsonIgnore
   @ManyToOne
   @JoinColumn(name = "user_id")
   private User user;

   @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,orphanRemoval = true)
   @JsonIgnore
   private List<OrderDetail> orderDetail;

   @OneToOne(mappedBy = "order", cascade = CascadeType.ALL,orphanRemoval = true)
   private Payments payment;

   @OneToOne(mappedBy = "order",cascade = CascadeType.ALL, orphanRemoval = true)
   private Address address;

}

