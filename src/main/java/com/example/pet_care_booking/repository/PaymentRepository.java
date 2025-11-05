package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.modal.Payments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payments, Long> {
    Optional<Payments> findByOrderId(Long orderId);
}
