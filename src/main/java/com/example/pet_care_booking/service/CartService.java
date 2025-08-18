package com.example.pet_care_booking.service;


import com.example.pet_care_booking.dto.CartDTO;
import com.example.pet_care_booking.dto.CartItemDTO;

public interface CartService {
   CartDTO getCartByUser(String email);
   void deleteCartByUser(String email);
   void addCart(String email, CartItemDTO cartItemDTO);
   void updateCartByUser(String email, CartItemDTO cartItemDTO);
   void deleteCartItem(String email, Long cartItemId);

   CartDTO getCartBySession(String sessionId);
   void addSession(String sessionId, CartItemDTO cartItemDTO);
   void mergeSession(String sessionId, String email);
   void deleteSession(String sessionId);
   void updateCartBySession(String sessionId, CartItemDTO cartItemDTO);
   void deleteCartItemSession(Long cartItemId, String sessionId);
}
