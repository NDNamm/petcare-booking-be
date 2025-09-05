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
import com.example.pet_care_booking.repository.*;
import com.example.pet_care_booking.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
   private final OrderRepository orderRepository;
   private final UserRepository userRepository;
   private final CartRepository cartRepository;
   private final ProductRepository productRepository;
   private final VariantRepository variantRepository;

    @Override
    public Page<OrderDTO> getAllOrders(String name, String phoneNumber, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orderPage;

        if (name == null && phoneNumber == null && status == null) {
            orderPage = orderRepository.findAll(pageable);
        } else {
            orderPage = orderRepository.searchOrders(name, phoneNumber, status, pageable);
        }

        return getOrder(orderPage);
    }

    private Page<OrderDTO> getOrder(Page<Order> orders) {
        return orders.map(order -> OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .sessionId(order.getSessionId())
                .fullName(order.getName())
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

      String name = user != null ? user.getUserName() : orderDTO.getFullName();
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
                .sessionId(sessionId)
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
    private List<OrderDetail> buildOrderDetails(Order order, Cart cart, List<ItemDTO> itemDTOs) {
        List<OrderDetail> orderDetails = new ArrayList<>();

      if (cart != null && !cart.getItems().isEmpty()) {
         for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                   .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            Variants variant = cartItem.getVariant();
            if (variant.getStock() < cartItem.getQuantity()) {
               throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);
            }
            variant.setStock(variant.getStock() - cartItem.getQuantity());

                if (!product.getStatus().equals(ProductStatus.AVAILABLE)) {
                    throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);
                }

//                if (product.getSl() <= 0) {
//                    product.setStatus(ProductStatus.OUT_OF_STOCK);
//                }


            OrderDetail orderDetail = OrderDetail.builder()
                   .order(order)
                   .product(product)
                   .variant(variant)
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
                Optional<Variants> variants = variantRepository.findBySizeAndProduct(dto.getSize(), product);
                if(variants.isEmpty()) throw new EntityNotFoundException("Not found variant with size and productId " +  product.getId());
                if (variants.get().getStock() < dto.getQuantity()) {
                    throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);
                }
                variants.get().setStock(variants.get().getStock() - dto.getQuantity());

                OrderDetail orderDetail = OrderDetail.builder()
                        .order(order)
                        .product(product)
                        .quantity(dto.getQuantity())
                        .price(dto.getPrice())
                        .variant(variants.get())
                        .totalPrice(dto.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())))
                        .build();

                orderDetails.add(orderDetail);
            }
        } else {
            throw new AppException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        return orderDetails;
    }
    @Override
    public Page<OrderDTO> getOrderClient(String userName, String sessionId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orders;

        if (userName != null && !"anonymousUser".equals(userName)) {
            userRepository.findUserByUserName(userName)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            orders = orderRepository.findOrdersByUser(userName, status, pageable);
        } else {
            orders = orderRepository.findOrdersBySession(sessionId, status, pageable);
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
        if (orderDTO.getFullName() != null) {
            order.setName(orderDTO.getFullName());
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
                        .size(items.getVariant().getSize())
                        .totalPrice(items.getTotalPrice())
                        .build();
                orderDetailDTOS.add(dto);
            }
        }
        return orderDetailDTOS;
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
