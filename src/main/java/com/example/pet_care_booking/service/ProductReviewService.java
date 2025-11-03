package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.ProductReviewDTO;
import com.example.pet_care_booking.enums.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ProductReviewService {
    Page<ProductReviewDTO> findAll(String phoneNumber, PageRequest pageRequest);
    Page<ProductReviewDTO> findByProductSlug(String slug, PageRequest pageRequest);
    void updateReviewStatus(Long productReviewId, ReviewStatus status);
    void createReview(ProductReviewDTO productReviewDTO);
}
