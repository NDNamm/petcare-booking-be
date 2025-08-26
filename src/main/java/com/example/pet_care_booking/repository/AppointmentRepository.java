package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.modal.Appointments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointments, Long> {

   @Query("""
          SELECT a from Appointments a
          Where (:nameOwer is null or lower(a.nameOwer) Like lowel(concat('%', :nameOwer, '%') ))
          AND (:phoneNumber is null or lower(a.phoneNumber) Like lowel(concat('%', :phoneNumber, '%') ))
          AND (:email is null or lower(a.email) Like lowel(concat('%', :email, '%') ))
          AND (:namePet is null or lower(a.petName) Like lowel(concat('%', :namePet, '%') ))
          AND (:nameVet is null or lower(a.veterinarian.name) Like lowel(concat('%', :nameVet, '%') ))
          AND (:status is null or lower(a.status) Like lowel(concat('%', :status, '%') ))
          """)
   Page<Appointments> searchAppointment(@Param("nameOwer") String nameOwer,
                                        @Param("phoneNumber") String phoneNumber,
                                        @Param("email") String email,
                                        @Param("namePet") String namePet,
                                        @Param("nameVet") String nameVet,
                                        @Param("status") String status,
                                        Pageable pageable);
}
