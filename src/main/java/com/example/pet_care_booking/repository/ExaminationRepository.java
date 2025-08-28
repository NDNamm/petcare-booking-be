package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.modal.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExaminationRepository extends JpaRepository<Examination, Long> {

   boolean existsByName(String name);
}
