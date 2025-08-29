package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.ItemDTO;
import com.example.pet_care_booking.dto.OrderDTO;
import org.springframework.data.domain.Page;

public interface OrderService {
   //Admin
   Page<OrderDTO> getAllOrders(String name, String phoneNumber, String status,int page, int size);
   void updateOrder(OrderDTO orderDTO, Long orderId);
   void deleteOrder(Long orderId);

   //Cua User
   void addOrder(OrderDTO orderDTO, String userName, String sessionId);
   Page<OrderDTO> getOrderClient(String userName, String sessionId, String status, int page, int size);
   void updateOrderByClient(OrderDTO orderDTO, Long orderId, String userName, String sessionId);
   void cancelOrder(Long orderId, String userName, String sessionId);
}
