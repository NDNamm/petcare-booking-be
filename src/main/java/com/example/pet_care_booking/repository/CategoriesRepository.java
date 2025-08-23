package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.modal.Categories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long> {
   Optional<Categories> findByNameCate(String nameCate);

   boolean existsByNameCate(String nameCate);
//    @Query("SELECT c FROM Categories c WHERE (:name is null or c.nameCate = :name)")
   Page<Categories> findByNameCateContainingIgnoreCase( String name, Pageable pageable);

}
