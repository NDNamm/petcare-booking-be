package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.modal.Veterinarians;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VeterinarianRepository extends JpaRepository<Veterinarians, Long> {

   @Query("""
          SELECT u FROM Veterinarians u
              WHERE (:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')))
                AND (:phoneNumber IS NULL OR LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :phoneNumber, '%')))
                AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
          """)
   Page<Veterinarians> searchVet(@Param("name") String userName,
                                 @Param("phoneNumber") String phoneNumber,
                                 @Param("email") String email,
                                 Pageable pageable);

  boolean existsByEmailAndPhoneNumber(String email, String phoneNumber);
}