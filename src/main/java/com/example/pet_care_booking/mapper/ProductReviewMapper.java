package com.example.pet_care_booking.mapper;

import com.example.pet_care_booking.dto.ProductReviewDTO;
import com.example.pet_care_booking.modal.ProductReview;
import org.springframework.stereotype.Component;

@Component
public class ProductReviewMapper {
    public ProductReviewDTO toDTO(ProductReview review) {
        return ProductReviewDTO.builder()
                .id(review.getId())
                .fullName(review.getUser().getUserName())
                .variant(review.getVariant().getSize())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getNamePro())
                .comment(review.getComment())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt().toString())
                .status(review.getStatus().toString())
                .build();
    }
}
