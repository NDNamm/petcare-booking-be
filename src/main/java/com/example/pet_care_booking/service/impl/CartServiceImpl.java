package com.example.pet_care_booking.service.impl;


import com.example.pet_care_booking.dto.CartDTO;
import com.example.pet_care_booking.dto.CartItemDTO;
import com.example.pet_care_booking.dto.ProductDTO;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.modal.Cart;
import com.example.pet_care_booking.modal.CartItem;
import com.example.pet_care_booking.modal.Product;
import com.example.pet_care_booking.modal.User;
import com.example.pet_care_booking.repository.CartItemRepository;
import com.example.pet_care_booking.repository.CartRepository;
import com.example.pet_care_booking.repository.ProductRepository;
import com.example.pet_care_booking.repository.UserRepository;
import com.example.pet_care_booking.service.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

   private final CartRepository cartRepository;
   private final UserRepository userRepository;
   private final ProductRepository productRepository;
   private final CartItemRepository cartItemRepository;

   //User
   @Override
   @Transactional
   public CartDTO getCartByUser(String userName) {
      User user = userRepository.findUserByUserName(userName)
             .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
      Cart cart = getCartUser(user);
      CartDTO cartDTO = CartDTO.builder()
             .id(cart.getId())
             .userId(user.getId())
             .createdAt(cart.getCreatedAt())
             .build();
      return getCartDTO(cart, cartDTO);
   }

   @Override
   @Transactional
   public void deleteCartByUser(String userName) {
      User user = userRepository.findUserByUserName(userName)
             .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
      Cart cart = cartRepository.findByUserWithItems(user);
      if (cart != null) {
         cart.getItems().clear();
         cartRepository.save(cart);
      }
   }

   @Override
   @Transactional
   public void addCart(String userName, CartItemDTO cartItemDTO) {
      User user = userRepository.findUserByUserName(userName)
             .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
      Cart cart = getCartUser(user);
      addProductToCart(cartItemDTO, cart);
   }

   @Override
   @Transactional
   public void updateCartByUser(String userName, CartItemDTO cartItemDTO) {
      User user = userRepository.findUserByUserName(userName)
             .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

      Cart cart = cartRepository.findByUserWithItems(user);
      if (cart == null) {
         throw new AppException(ErrorCode.CART_NOT_FOUND);
      }
      updateCart(cartItemDTO, cart);
   }

   @Override
   @Transactional
   public void deleteCartItem(String userName, Long cartItemId) {
      User user = userRepository.findUserByUserName(userName)
             .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

      Cart cart = cartRepository.findByUserWithItems(user);
      deleteItems(cartItemId, cart);
   }


   //Session
   @Override
   @Transactional
   public CartDTO getCartBySession(String sessionId) {
      Cart cart = cartRepository.findBySessionIdWithItems(sessionId);
      if (cart == null) {
         cart = new Cart();
         cart.setSessionId(sessionId);
         cart.setCreatedAt(LocalDateTime.now());
         cartRepository.save(cart);
      }
      CartDTO cartDTO = new CartDTO();
      cartDTO.setId(cart.getId());
      cartDTO.setCreatedAt(cart.getCreatedAt());

      return getCartDTO(cart, cartDTO);
   }

   @Override
   @Transactional
   public void addSession(String sessionId, CartItemDTO cartItemDTO) {
      Cart cart = getCartSession(sessionId);
      addProductToCart(cartItemDTO, cart);
   }

   @Override
   @Transactional
   public void mergeSession(String sessionId, String userName) {
      Cart cart = cartRepository.findBySessionIdWithItems(sessionId);
      if (cart == null || cart.getItems().isEmpty()) return;

      User user = userRepository.findUserByUserName(userName)
             .orElseThrow(() -> new RuntimeException("User not found"));

      Cart userCart = cartRepository.findByUserWithItems(user);
      if (userCart == null) {
         userCart = new Cart();
         userCart.setUser(user);
         userCart.setCreatedAt(LocalDateTime.now());
         cartRepository.save(userCart);
      }

      for (CartItem cartItem : cart.getItems()) {
         Optional<CartItem> existing = userCart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(cartItem.getProduct().getId()))
                .findFirst();

         if (existing.isPresent()) {
            CartItem existItem = existing.get();
            existItem.setQuantity(existItem.getQuantity() + cartItem.getQuantity());
            existItem.setTotalPrice(existItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            cartItemRepository.save(existItem);
         } else {
            CartItem cartItems = new CartItem();
            cartItems.setCart(userCart);
            cartItems.setProduct(cartItem.getProduct());
            cartItems.setQuantity(cartItem.getQuantity());
            cartItems.setPrice(cartItem.getPrice());
            cartItems.setTotalPrice(cartItem.getTotalPrice());
            cartItemRepository.save(cartItems);
         }

         cartRepository.save(userCart);
         cartItemRepository.deleteAll(cart.getItems());
         cartRepository.delete(cart);
      }
   }

   @Override
   @Transactional
   public void deleteSession(String sessionId) {
      Cart cartSession = cartRepository.findBySessionIdWithItems(sessionId);
      if (cartSession != null) {
         cartSession.getItems().clear();
         cartRepository.save(cartSession);
      } else {
         throw new AppException(ErrorCode.SESSION_NOT_FOUND);
      }

   }

   @Override
   @Transactional
   public void updateCartBySession(String sessionId, CartItemDTO cartItemDTO) {
      Cart cart = cartRepository.findBySessionIdWithItems(sessionId);
      if (cart != null) {
         updateCart(cartItemDTO, cart);
      } else {
         throw new AppException(ErrorCode.SESSION_NOT_FOUND);
      }
   }

   @Override
   @Transactional
   public void deleteCartItemSession(Long cartItemId, String sessionId) {
      Cart cart = cartRepository.findBySessionIdWithItems(sessionId);
      if (cart != null) {
         deleteItems(cartItemId, cart);
      } else {
         throw new AppException(ErrorCode.SESSION_NOT_FOUND);
      }
   }

   private Cart getCartUser(User user) {
      Cart cart = cartRepository.findByUserWithItems(user);
      if (cart == null) {
         cart = new Cart();
         cart.setUser(user);
         cart.setCreatedAt(LocalDateTime.now());
         cart = cartRepository.save(cart);
      }
      return cart;
   }

   private CartDTO getCartDTO(Cart cart, CartDTO cartDTO) {
      List<CartItemDTO> cartItemDTOS = new ArrayList<>();

      for (CartItem items : cart.getItems()) {
         Product product = items.getProduct();
         ProductDTO productDTO = ProductDTO.builder()
                .id(product.getId())
                .namePro(product.getNamePro())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .status(product.getStatus())
                .build();

         CartItemDTO cartItemDTO = CartItemDTO.builder()
                .id(items.getId())
                .productId(items.getProduct().getId())
                .quantity(items.getQuantity())
                .price(items.getPrice())
                .totalPrice(items.getTotalPrice())
                .product(productDTO)
                .build();
         cartItemDTOS.add(cartItemDTO);
      }
      cartDTO.setCartItems(cartItemDTOS);
      return cartDTO;
   }

   private void addProductToCart(CartItemDTO cartItemDTO, Cart cart) {
      Product product = productRepository.findById(cartItemDTO.getProductId())
             .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

      Optional<CartItem> existingItem = cart.getItems().stream()
             .filter(item -> item.getProduct().getId().equals(product.getId()))
             .findFirst();

      CartItem cartItem;
      Long addedQuantity = cartItemDTO.getQuantity();
      BigDecimal itemPrice = cartItemDTO.getPrice();

      if (existingItem.isPresent()) {
         cartItem = existingItem.get();
         long newQuantity = cartItem.getQuantity() + cartItemDTO.getQuantity();
         cartItem.setQuantity(newQuantity);
         cartItem.updateQuantityAndPrice(newQuantity, cartItem.getPrice());
      } else {
         cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(cartItemDTO.getQuantity())
                .price(cartItemDTO.getPrice())
                .build();
         cartItem.updateQuantityAndPrice(addedQuantity, itemPrice);
      }

      cartItemRepository.save(cartItem);
      cartRepository.save(cart);
   }

   private void updateCart(CartItemDTO cartItemDTO, Cart cart) {
      Product product = productRepository.findById(cartItemDTO.getProductId())
             .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

      Optional<CartItem> existingItem = cart.getItems().stream()
             .filter(item -> item.getProduct().getId().equals(product.getId()))
             .findFirst();

      if (existingItem.isPresent()) {
         CartItem cartItem = existingItem.get();
         cartItem.setQuantity(cartItemDTO.getQuantity());
         cartItem.setPrice(cartItemDTO.getPrice());
         cartItem.updateQuantityAndPrice(cartItemDTO.getQuantity(), cartItemDTO.getPrice());

         cartItemRepository.save(cartItem);
      } else {
         throw new AppException(ErrorCode.CART_ITEM_NOT_FOUND);
      }

      cartRepository.save(cart);
   }

   private void deleteItems(Long cartItemId, Cart cart) {
      if (cart == null) {
         throw new AppException(ErrorCode.CART_NOT_FOUND);
      }
      CartItem cartItems = cartItemRepository.findById(cartItemId)
             .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

      if (!cart.getItems().contains(cartItems)) {
         throw new AppException(ErrorCode.UNAUTHORIZED_OPERATION);
      }
      cart.getItems().remove(cartItems);
      cartRepository.save(cart);
   }


   //Ham Session
   private Cart getCartSession(String sessionId) {
      Cart cart = cartRepository.findBySessionIdWithItems(sessionId);
      if (cart == null) {
         cart = new Cart();
         cart.setSessionId(sessionId);
         cart.setCreatedAt(LocalDateTime.now());
         cartRepository.save(cart);
      }
      return cart;
   }
}
