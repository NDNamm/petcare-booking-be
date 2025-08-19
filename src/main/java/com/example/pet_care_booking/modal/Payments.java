package com.example.pet_care_booking.modal;

import com.example.pet_care_booking.modal.enums.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "payments")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payments {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long id;

   @Column(name = "payment_date")
   private LocalDate paymentDate;

   @Column(name = "amount", precision = 10, scale = 2, nullable = false)
   private BigDecimal amount;

   @Column(name = "payment_method", nullable = false)
   @Enumerated(EnumType.STRING)
   private PaymentMethod paymentMethod;

   @OneToOne
   @JoinColumn(name = "order_id", unique = true)
   @JsonIgnore
   private Order order;

}
