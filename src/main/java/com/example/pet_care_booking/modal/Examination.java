package com.example.pet_care_booking.modal;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "examination")
public class Examination {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "name")
   private String name;

   @Column(name = "price", precision = 10, scale = 2)
   private BigDecimal price;

   @Column(name = "description")
   private String description;

   @Column(name = "created_at")
   private LocalDateTime createdAt;

   @ManyToMany(mappedBy = "examination")
   @JsonIgnore
   private List<Appointments> appointments;

}
