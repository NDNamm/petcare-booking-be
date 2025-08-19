package com.example.pet_care_booking.dto;

import com.example.pet_care_booking.modal.enums.OrderStatus;
import com.example.pet_care_booking.modal.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
public class OrderDTO {
   private Long id;
   private String sessionId;
   private Long userId;
   private String name;
   private String phoneNumber;
   private BigDecimal totalAmount;
   private LocalDateTime orderDate;
   private OrderStatus status;
   private String note;
   private PaymentMethod paymentMethod;
   private List<OrderDetailDTO> orderDetailDTO;
   private List<ItemDTO> items;
   private AddressDTO addressDTO;

}
