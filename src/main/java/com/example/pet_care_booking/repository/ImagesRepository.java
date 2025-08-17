package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.modal.Images;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagesRepository extends CrudRepository<Images, Long> {
}
