package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.modal.Appointments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointments, Long> {

    @Query("""
             SELECT a FROM Appointments a
             LEFT JOIN a.veterinarian v
             WHERE (:ownerName IS NULL OR LOWER(a.ownerName) LIKE LOWER(CONCAT('%', :ownerName, '%')))
             AND (:phoneNumber IS NULL OR LOWER(a.phoneNumber) LIKE LOWER(CONCAT('%', :phoneNumber, '%')))
             AND (:email IS NULL OR LOWER(a.email) LIKE LOWER(CONCAT('%', :email, '%')))
             AND (:petName IS NULL OR LOWER(a.petName) LIKE LOWER(CONCAT('%', :petName, '%')))
             AND (:vetName IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :vetName, '%')))
            AND (:status IS NULL OR LOWER(a.appointStatus) LIKE LOWER(CONCAT('%', :status, '%')))
            """)
    Page<Appointments> searchAppointment(@Param("ownerName") String ownerName,
                                         @Param("phoneNumber") String phoneNumber,
                                         @Param("email") String email,
                                         @Param("petName") String petName,
                                         @Param("vetName") String vetName,
                                         @Param("status") String status,
                                         Pageable pageable);

    @Query("""
            Select a from Appointments a
            where :userName = a.user.userName
            AND (:status IS NULL OR LOWER(a.appointStatus) LIKE LOWER(CONCAT('%', :status, '%')))
            """)
    Page<Appointments> searchAppointmentsByUser(@Param("userName") String userName,
                                                @Param("status") String status,
                                                Pageable pageable);

    @Query("""
            Select a from Appointments a
            where :sessionId = a.sessionId
            AND (:status IS NULL OR LOWER(a.appointStatus) LIKE LOWER(CONCAT('%', :status, '%')))
            """)
    Page<Appointments> searchAppointmentsBySessionId(@Param("sessionId") String sessionId,
                                                     @Param("status") String status,
                                                     Pageable pageable);

    @Query("SELECT a FROM Appointments a " +
            "WHERE a.startTime < :end AND a.endTime > :start "
            )
    List<Appointments> findConflictingAppointments(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

}

