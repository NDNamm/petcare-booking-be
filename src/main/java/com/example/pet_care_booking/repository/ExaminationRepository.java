package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.modal.Examination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ExaminationRepository extends JpaRepository<Examination, Long> {

   boolean existsByName(String name);

   @Query("""
          SELECT e from Examination e
          where (:name is null or e.name like concat('%', :name, '%') )
          and (:min is null or e.price >= :min)
          and (:max is null or e.price <= :max)
          """)
   Page<Examination> searchExm(@Param("name") String name,
                               @Param("min") BigDecimal min,
                               @Param("max") BigDecimal  max,
                               Pageable pageable);
}
