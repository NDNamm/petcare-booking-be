package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.modal.Product;
import com.example.pet_care_booking.modal.Variants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface VariantRepository extends JpaRepository<Variants, Long> {
    Optional<Variants> findBySizeAndProduct(String size, Product product);
    List<Variants> findByProduct(Product product);
    @Query("SELECT sum(v.stock) FROM Variants v where v.product = :product")
    Long sumByStockAndProduct(@Param("product") Product product);
    @Query("SELECT v.price FROM Variants v WHERE v.product = :product and v.size = :size")
    BigDecimal findPriceBySizeAndProduct(@Param("size") String size,
                                         @Param("product") Product product);
    Long countBySizeAndProduct(String size, Product product);
}
