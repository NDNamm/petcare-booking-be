package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.modal.Cart;
import com.example.pet_care_booking.modal.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
}
