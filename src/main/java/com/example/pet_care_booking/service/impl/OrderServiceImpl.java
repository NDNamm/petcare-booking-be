package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.AddressDTO;
import com.example.pet_care_booking.dto.ItemDTO;
import com.example.pet_care_booking.dto.OrderDTO;
import com.example.pet_care_booking.dto.OrderDetailDTO;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.modal.*;
import com.example.pet_care_booking.modal.enums.OrderStatus;
import com.example.pet_care_booking.modal.enums.PaymentMethod;
import com.example.pet_care_booking.modal.enums.ProductStatus;
import com.example.pet_care_booking.repository.CartRepository;
import com.example.pet_care_booking.repository.OrderRepository;
import com.example.pet_care_booking.repository.ProductRepository;
import com.example.pet_care_booking.repository.UserRepository;
import com.example.pet_care_booking.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
   private final OrderRepository orderRepository;
   private final UserRepository userRepository;
   private final CartRepository cartRepository;
   private final ProductRepository productRepository;

   @Override
   public Page<OrderDTO> getAllOrders(String name, String phoneNumber, String address, String status, int page, int size) {
      Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
      Page<Order> orderPage;

      if (name == null || name.isEmpty() && phoneNumber == null || phoneNumber.isEmpty() && address == null || address.isEmpty()) {
         orderPage = orderRepository.findAll(pageable);
      } else {
         orderPage = orderRepository.searchOrders(name, phoneNumber, address, status, pageable);
      }

      return getOrder(orderPage);
   }

   private Page<OrderDTO> getOrder(Page<Order> orders) {
      return orders.map(order -> OrderDTO.builder()
             .id(order.getId())
             .userId(order.getUser().getId())
             .name(order.getName())
             .phoneNumber(order.getPhoneNumber())
             .status(order.getStatus())
             .totalAmount(order.getTotalAmount())
             .orderDetailDTO(getOrderDetailDTOS(order))
             .addressDTO(order.getAddress() != null ? mapToAddressDTO(order.getAddress()) : null)
             .note(order.getNote())
             .paymentMethod(order.getPayment().getPaymentMethod())
             .orderDate(order.getOrderDate())
             .build());
   }

   private AddressDTO mapToAddressDTO(Address address) {
      return AddressDTO.builder()
             .id(address.getId())
             .homeAddress(address.getHomeAddress())
             .city(address.getCity())
             .district(address.getDistrict())
             .commune(address.getCommune())
             .build();
   }

   @Override
   public void updateOrder(OrderDTO orderDTO, Long orderId) {
      Order order = orderRepository.findById(orderId)
             .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

      order.setStatus(orderDTO.getStatus());
      orderRepository.save(order);
   }

   @Override
   public void deleteOrder(Long orderId) {
      Order order = orderRepository.findById(orderId)
             .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

      orderRepository.delete(order);
   }

   @Override
   @Transactional
   public void addOrder(OrderDTO orderDTO, String userName, String sessionId) {
      User user = null;
      Cart cart;

      if (userName != null && !userName.equals("anonymousUser")) {
         user = userRepository.findUserByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
         cart = cartRepository.findByUser(user);
      } else if (sessionId != null) {
         cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new AppException(ErrorCode.SESSION_NOT_FOUND));
      } else {
         throw new AppException(ErrorCode.NOT_FOUND);
      }

      if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
         throw new AppException(ErrorCode.CART_NOT_FOUND);
      }

      String name = user != null ? user.getUserName() : orderDTO.getName();
      String phone = user != null ? user.getPhoneNumber() : orderDTO.getPhoneNumber();

      BigDecimal totalAmount = cart.getItems().stream()
             .map(CartItem::getTotalPrice)
             .reduce(BigDecimal.ZERO, BigDecimal::add);

      Order order = Order.builder()
             .user(user)
             .sessionId(user == null ? sessionId : null)
             .name(name)
             .phoneNumber(phone)
             .orderDate(LocalDateTime.now())
             .totalAmount(totalAmount)
             .status(OrderStatus.PENDING)
             .note(orderDTO.getNote())
             .build();

      List<OrderDetail> orderDetail = buildOrderDetails(order, cart, orderDTO.getItems());
      order.setOrderDetail(orderDetail);

      Address address = Address.builder()
             .homeAddress(orderDTO.getAddressDTO().getHomeAddress())
             .city(orderDTO.getAddressDTO().getCity())
             .district(orderDTO.getAddressDTO().getDistrict())
             .commune(orderDTO.getAddressDTO().getCommune())
//             .sessionId(sessionId)
             .order(order)
             .user(user)
             .build();
      order.setAddress(address);

      Payments payments = Payments.builder()
             .amount(order.getTotalAmount())
             .paymentDate(LocalDate.now())
             .order(order)
             .build();
      PaymentMethod method = PaymentMethod.COD;
      if (method != null) {
         payments.setPaymentMethod(method);
      } else {
         throw new AppException(ErrorCode.NOT_PAYMENT_METHOD);
      }
      order.setPayment(payments);

      orderRepository.save(order);
   }

   @Override
   public Page<OrderDTO> getOrderClient(String userName, String sessionId, String status, int page, int size) {
      Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
      Page<Order> orders;

      if (status != null) {
         orders = orderRepository.findOrders(userName, sessionId, status, pageable);
      } else {
         orders = orderRepository.findAll(pageable);
      }

      return getOrder(orders);
   }

   @Override
   public void updateOrderByClient(OrderDTO orderDTO, Long orderId, String userName, String sessionId) {
      Order order = orderRepository.findById(orderId)
             .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

      validateOrderOwnership(order, userName, sessionId);

      if (order.getStatus() != OrderStatus.PENDING) {
         throw new AppException(ErrorCode.ORDER_CANNOT_BE_MODIFIED);
      }
      if (orderDTO.getNote() != null) {
         order.setNote(orderDTO.getNote());
      }
      if (orderDTO.getPhoneNumber() != null) {
         order.setPhoneNumber(orderDTO.getPhoneNumber());
      }
      if (orderDTO.getName() != null) {
         order.setName(orderDTO.getName());
      }
      if (orderDTO.getAddressDTO() != null) {
         Address address = Address.builder()
                .homeAddress(orderDTO.getAddressDTO().getHomeAddress())
                .city(orderDTO.getAddressDTO().getCity())
                .district(orderDTO.getAddressDTO().getDistrict())
                .commune(orderDTO.getAddressDTO().getCommune())
                .build();

         order.setAddress(address);
      }

      orderRepository.save(order);
   }

   @Override
   public void cancelOrder(Long orderId, String userName, String sessionId) {
      Order order = orderRepository.findById(orderId)
             .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

      validateOrderOwnership(order, userName, sessionId);
      if (order.getStatus() != OrderStatus.PENDING)
         throw new AppException(ErrorCode.ORDER_CANNOT_BE_MODIFIED);

      order.setStatus(OrderStatus.CANCELED);
      orderRepository.save(order);
   }

   private static List<OrderDetailDTO> getOrderDetailDTOS(Order order) {
      List<OrderDetailDTO> orderDetailDTOS = new ArrayList<>();
      if (order.getOrderDetail() != null) {
         for (OrderDetail items : order.getOrderDetail()) {
            OrderDetailDTO dto = OrderDetailDTO.builder()
                   .id(items.getId())
                   .urlProductImage(items.getProduct().getImageUrl())
                   .productName(items.getProduct().getNamePro())
                   .quantity(items.getQuantity())
                   .price(items.getPrice())
                   .totalPrice(items.getTotalPrice())
                   .build();
            orderDetailDTOS.add(dto);
         }
      }
      return orderDetailDTOS;
   }

   private List<OrderDetail> buildOrderDetails(Order order, Cart cart, List<ItemDTO> itemDTOs) {
      List<OrderDetail> orderDetails = new ArrayList<>();

      if (cart != null && !cart.getItems().isEmpty()) {
         for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                   .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            if (!product.getStatus().equals(ProductStatus.AVAILABLE)) {
               throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);
            }

            OrderDetail orderDetail = OrderDetail.builder()
                   .order(order)
                   .product(product)
                   .quantity(cartItem.getQuantity())
                   .price(cartItem.getPrice())
                   .totalPrice(cartItem.getTotalPrice())
                   .build();

            orderDetails.add(orderDetail);
         }

      } else if (itemDTOs != null && !itemDTOs.isEmpty()) {
         for (ItemDTO dto : itemDTOs) {
            Product product = productRepository.findById(dto.getProductId())
                   .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            if (!product.getStatus().equals(ProductStatus.AVAILABLE)) {
               throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);
            }

            OrderDetail orderDetail = OrderDetail.builder()
                   .order(order)
                   .product(product)
                   .quantity(dto.getQuantity())
                   .price(dto.getPrice())
                   .totalPrice(dto.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())))
                   .build();

            orderDetails.add(orderDetail);
         }
      } else {
         throw new AppException(ErrorCode.CART_ITEM_NOT_FOUND);
      }

      return orderDetails;
   }

   private void validateOrderOwnership(Order order, String userName, String sessionId) {
      if (userName != null && !userName.equals("anonymousUser")) {
         User user = userRepository.findUserByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

         boolean isOwner = order.getUser() != null && order.getUser().getId().equals(user.getId());
         if (!isOwner) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
         }

      } else if (sessionId != null) {
         boolean isSessionMatch = sessionId.equals(order.getSessionId());
         if (!isSessionMatch) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
         }

      } else {
         throw new AppException(ErrorCode.UNAUTHORIZED);
      }
   }

}
