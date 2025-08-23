package com.example.pet_care_booking.repository;

import com.example.pet_care_booking.modal.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

   @Query("""
              SELECT o FROM Order o
              WHERE (:name IS NULL OR LOWER(o.name) LIKE LOWER(CONCAT('%', :name, '%')))
                AND (:phoneNumber IS NULL OR LOWER(o.phoneNumber) LIKE LOWER(CONCAT('%', :phoneNumber, '%')))
                AND (:status IS NULL OR LOWER(o.status) LIKE LOWER(CONCAT('%', :status, '%')))
       
          """)
   Page<Order> searchOrders(
          @Param("name") String name,
          @Param("phoneNumber") String phoneNumber,
          @Param("status") String status,
          Pageable pageable
   );


   @Query("SELECT o FROM Order o " +
          "WHERE (:name IS NULL OR o.name = :name) " +
          "AND (:sessionId IS NULL OR o.sessionId = :sessionId) " +
          "AND (:status IS NULL OR o.status = :status)")
   Page<Order> findOrders(@Param("name") String name,
                          @Param("sessionId") String sessionId,
                          @Param("status") String status,
                          Pageable pageable);


}
