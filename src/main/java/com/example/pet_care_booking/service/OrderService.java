package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.OrderDTO;
import org.springframework.data.domain.Page;

public interface OrderService {
   //Admin
   Page<OrderDTO> getAllOrders(String name, String phoneNumber, String status,int page, int size);
   void updateOrder(OrderDTO orderDTO, Long orderId);
   void deleteOrder(Long orderId);

   //Cua User
   void addOrder(OrderDTO orderDTO, String email, String sessionId);
   Page<OrderDTO> getOrderClient(String email, String sessionId, String status, int page, int size);
   void updateOrderByClient(OrderDTO orderDTO, Long orderId, String email, String sessionId);
   void cancelOrder(Long orderId, String email, String sessionId);
}
