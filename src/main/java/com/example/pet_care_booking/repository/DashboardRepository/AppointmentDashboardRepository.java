package com.example.pet_care_booking.repository.DashboardRepository;

import com.example.pet_care_booking.dto.Dashboard.AppointmentDashboardDTO;
import com.example.pet_care_booking.modal.Appointments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentDashboardRepository extends JpaRepository<Appointments, Long> {

   @Query("""
       SELECT SUM(a.totalPrice)
       FROM Appointments a
       WHERE a.appointStatus <> 'CANCELED'
       """)
   BigDecimal getTotalRevenue();


   @Query("""
          SELECT new com.example.pet_care_booking.dto.Dashboard.AppointmentDashboardDTO(FUNCTION('date', a.createdAt), SUM(a.totalPrice), COUNT(a.id)) \
          FROM Appointments a \
          WHERE a.createdAt BETWEEN :start AND :end \
          AND a.appointStatus <> 'CANCELED'
          GROUP BY FUNCTION('date', a.createdAt) \
          ORDER BY FUNCTION('date', a.createdAt)""")
   List<AppointmentDashboardDTO> getRevenueByDay(LocalDateTime start, LocalDateTime end);

   @Query("""
          SELECT new com.example.pet_care_booking.dto.Dashboard.AppointmentDashboardDTO(MONTH(o.createdAt), SUM(o.totalPrice),  COUNT(o.id))
          FROM Appointments o
          WHERE YEAR(o.createdAt) = :year
          AND o.appointStatus <> 'CANCELED'
          GROUP BY MONTH(o.createdAt)
          ORDER BY MONTH(o.createdAt)""")
   List<AppointmentDashboardDTO> getRevenueByMonth(int year);

   @Query("SELECT o.appointStatus, COUNT(o) FROM Appointments o GROUP BY o.appointStatus")
   List<Object[]> getRevenueByStatus();

   @Query("""
       SELECT new com.example.pet_care_booking.dto.Dashboard.AppointmentDashboardDTO(
           e.name,
           COUNT(a.id)
       )
       FROM Appointments a
       JOIN a.examination e
       WHERE a.appointStatus <> 'CANCELED'
       GROUP BY e.name
       ORDER BY COUNT(a.id) DESC
       """)
   List<AppointmentDashboardDTO> getTopExamination();
}

