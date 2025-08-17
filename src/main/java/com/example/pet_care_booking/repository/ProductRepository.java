package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.modal.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

   Optional<Product> findByNamePro(String namePro);

   boolean existsByNamePro(String namePro);

   @Query("select p from Product p where lower(p.namePro) LIKE lower(concat('%', :namePro, '%'))")
   Page<Product> findByNamePro(@Param("namePro") String namePro, Pageable pageable);

   Page<Product> findProductsByCategoryId(Long categoryId, Pageable pageable);
}
