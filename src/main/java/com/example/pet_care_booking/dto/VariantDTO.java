package com.example.pet_care_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VariantDTO {
    private Long id;
    private String size;
    private BigDecimal price;
    private Long stock;
    private Long productId;
}
