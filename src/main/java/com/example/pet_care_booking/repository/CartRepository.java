package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.modal.Cart;
import com.example.pet_care_booking.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
   Cart findByUser(User user);

   @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.user = :user")
   Cart findByUserWithItems(@Param("user") User user);

   @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.sessionId = :sessionId")
   Cart findBySessionIdWithItems(@Param("sessionId") String sessionId);

   Optional<Cart> findBySessionId(String sessionId);
}
