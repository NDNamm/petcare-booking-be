package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.modal.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    @Query("SELECT rv from ProductReview  rv where (:phoneNumber is null or rv.user.phoneNumber LIKE CONCAT('%', :phoneNumber, '%'))")
    Page<ProductReview> findProductReviewByPhoneNumber(@Param("phoneNumber") String phoneNumber, Pageable pageable);

    @Query("SELECT rv from ProductReview  rv where rv.product.slug LIKE %:slug%")
    Page<ProductReview> findProductReviewBySlug(@Param("slug") String slug, Pageable pageable);

    boolean existsByOrderAndProductAndUser(Order order, Product product, User user);
    @Query("SELECT COALESCE(AVG(rv.rating), 0) " +
            "FROM ProductReview rv " +
            "WHERE rv.product = :product AND rv.status = 'APPROVED'")
    Double averageProductReviewByProduct(@Param("product") Product product);

    ProductReview findProductReviewByProductAndVariantAndUserAndOrder(Product product, Variants variant, User user, Order order);
}
