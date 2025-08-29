package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.modal.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByNamePro(String namePro);

    boolean existsByNamePro(String namePro);

    Product findBySlug(String slug);

    Page<Product> findProductsByCategoryId(Long categoryId, Pageable pageable);

    @Query("""
            SELECT DISTINCT p
            FROM Product p
            JOIN p.variants v
            WHERE (:keyword IS NULL OR p.namePro LIKE CONCAT('%', :keyword, '%'))
              AND (:categoryId IS NULL OR p.category.id = :categoryId)
              AND (:size IS NULL OR v.size = :size)
              AND (:minPrice IS NULL OR v.price >= :minPrice)
              AND (:maxPrice IS NULL OR v.price <= :maxPrice)
            """)
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("size") String size,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

}
