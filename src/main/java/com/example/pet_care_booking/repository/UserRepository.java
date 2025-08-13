package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

   Optional<UserEntity> findUserByUserName(String userName);
   Optional<UserEntity> findUserByEmail(String email);
   Optional<UserEntity> findUserByPhoneNumber(String phoneNumber);
}
