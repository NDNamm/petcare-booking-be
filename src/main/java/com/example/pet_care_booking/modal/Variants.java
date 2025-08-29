package com.example.pet_care_booking.modal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Table(name = "variants")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Variants {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String size;
    @Column()
    private Long stock;
    @Column()
    private BigDecimal price;

    @ManyToOne()
    @JoinColumn(name = "product_id")
    private Product product;
    @OneToMany(mappedBy = "variant")
    private List<CartItem> cartItems;



}