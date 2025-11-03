package com.example.pet_care_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductReviewDTO {
    private Long id;
    private Long userId;
    private String fullName;
    private Long productId;
    private String variant;
    private Long variantId;
    private String productName;
    private Long orderId;
    private Integer rating;
    private String comment;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
