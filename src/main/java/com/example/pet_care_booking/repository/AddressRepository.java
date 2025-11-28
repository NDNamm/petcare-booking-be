package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.modal.Address;
import com.example.pet_care_booking.modal.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address,Long> {
    Optional<Address> findByOrder(Order order);
}
