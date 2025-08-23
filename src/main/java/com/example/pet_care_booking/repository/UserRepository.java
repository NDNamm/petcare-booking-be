package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.modal.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

   Optional<User> findUserByUserName(String userName);
   Optional<User> findUserByEmail(String email);
   Optional<User> findUserByPhoneNumber(String phoneNumber);

   @Query("""
          SELECT u FROM User u
              WHERE (:userName IS NULL OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :userName, '%')))
                AND (:phoneNumber IS NULL OR LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :phoneNumber, '%')))
                AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
          """)
   Page<User> searchUser(@Param("userName") String userName,
                         @Param("phoneNumber") String phoneNumber,
                         @Param("email") String email,
                         Pageable pageable);
}
