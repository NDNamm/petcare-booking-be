package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.modal.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

   Optional<Product> findByNamePro(String namePro);

   boolean existsByNamePro(String namePro);
   Product findBySlug(String slug);
   Page<Product> findProductsByCategoryId(Long categoryId, Pageable pageable);

   Page<Product> findByNameProContainingIgnoreCase(String keyword, Pageable pageable);
}
