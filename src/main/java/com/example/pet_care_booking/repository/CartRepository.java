package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.modal.Cart;
import com.example.pet_care_booking.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

}
