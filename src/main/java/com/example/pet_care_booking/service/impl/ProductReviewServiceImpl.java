package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.ProductReviewDTO;
import com.example.pet_care_booking.enums.ReviewStatus;
import com.example.pet_care_booking.mapper.ProductReviewMapper;
import com.example.pet_care_booking.modal.*;
import com.example.pet_care_booking.modal.enums.OrderStatus;
import com.example.pet_care_booking.repository.OrderRepository;
import com.example.pet_care_booking.repository.ProductRepository;
import com.example.pet_care_booking.repository.ProductReviewRepository;
import com.example.pet_care_booking.repository.VariantRepository;
import com.example.pet_care_booking.service.ProductReviewService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductReviewServiceImpl implements ProductReviewService {
    private final ProductReviewRepository productReviewRepository;
    private final ProductReviewMapper productReviewMapper;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final VariantRepository variantRepository;
    private final CurrentUserServiceImpl currentUserServiceImpl;

    @Override
    public Page<ProductReviewDTO> findAll(String phoneNumber, PageRequest pageRequest) {
        Page<ProductReview> products = productReviewRepository.findProductReviewByPhoneNumber(phoneNumber, pageRequest);
        return products.map(productReviewMapper::toDTO);
    }

    @Override
    @Cacheable(cacheNames = "review", key = "#slug")
    public Page<ProductReviewDTO> findByProductSlug(String slug, PageRequest pageRequest) {
        Page<ProductReview> products = productReviewRepository.findProductReviewBySlug(slug, pageRequest);
        return products.map(productReviewMapper::toDTO);
    }

    @Override
    @Transactional
    public void updateReviewStatus(Long productReviewId, ReviewStatus status) {
        ProductReview productReview = productReviewRepository.findById(productReviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        productReview.setStatus(status);
        productReviewRepository.save(productReview);
    }


    @Override
    @Transactional
    public void createReview(ProductReviewDTO productReviewDTO) {
        Order order = orderRepository.findById(productReviewDTO.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order Not Found"));
        Product product = productRepository.findById(productReviewDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product Not Found"));
        Variants variants = variantRepository.findById(productReviewDTO.getVariantId())
                .orElseThrow(() -> new IllegalArgumentException("Variant Not Found"));
        User user = currentUserServiceImpl.getCurrentUser();
        if(!order.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("You cannot review this order");
        }
        if(!order.getStatus().equals(OrderStatus.COMPLETED)){
            throw new IllegalStateException("Order Status Not Complete");
        }
        boolean exists = productReviewRepository.existsByOrderAndProductAndUser(order, product, user);
        if (exists) {
            throw new IllegalStateException("You have already reviewed this product.");
        }
        ProductReview productReview = ProductReview.builder()
                .product(product)
                .order(order)
                .createdAt(LocalDateTime.now())
                .comment(productReviewDTO.getComment())
                .rating(productReviewDTO.getRating())
                .status(ReviewStatus.PENDING)
                .user(user)
                .variant(variants)
                .build();
        productReviewRepository.save(productReview);
        log.info("User {} is creating a review for product {}", user.getPhoneNumber(), product.getNamePro());
    }


}
