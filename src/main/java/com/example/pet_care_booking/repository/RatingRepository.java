package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.modal.Product;
import com.example.pet_care_booking.modal.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

   Page<Rating> findRatingByProduct(Product product, Pageable pageable);

   Optional<Rating> findRatingByProductIdAndUserId(Long product, Long userId);
}
