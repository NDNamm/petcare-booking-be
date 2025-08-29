package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.dto.DashboardDTO;
import com.example.pet_care_booking.modal.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<Order, Long> {

   @Query("""
          SELECT new com.example.pet_care_booking.dto.DashboardDTO(FUNCTION('date', o.orderDate), SUM(o.totalAmount), COUNT(o.id)) \
          FROM Order o \
          WHERE o.orderDate BETWEEN :start AND :end \
          AND o.status <> 'CANCELED'
          GROUP BY FUNCTION('date', o.orderDate) \
          ORDER BY FUNCTION('date', o.orderDate)""")
   List<DashboardDTO> getRevenueByDay(LocalDateTime start, LocalDateTime end);

   @Query("""
          SELECT new com.example.pet_care_booking.dto.DashboardDTO(MONTH(o.orderDate), SUM(o.totalAmount),  COUNT(o.id))
          FROM Order o
          WHERE YEAR(o.orderDate) = :year
          AND o.status <> 'CANCELED'
          GROUP BY MONTH(o.orderDate)
          ORDER BY MONTH(o.orderDate)""")
   List<DashboardDTO> getRevenueByMonth(int year);

   @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
   List<Object[]> getRevenueByStatus();

   @Query("""
          SELECT new com.example.pet_care_booking.dto.DashboardDTO(o.product.namePro,sum(o.quantity) )
          FROM OrderDetail o
          GROUP BY o.product.namePro
          ORDER BY SUM(o.quantity) DESC""")
   List<DashboardDTO> getTopProduct();
}

