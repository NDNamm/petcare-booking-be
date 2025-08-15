package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

   Optional<User> findUserByUserName(String userName);
   Optional<User> findUserByEmail(String email);
   Optional<User> findUserByPhoneNumber(String phoneNumber);
}
