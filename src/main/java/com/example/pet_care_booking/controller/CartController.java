package com.example.pet_care_booking.controller;

import com.example.pet_care_booking.dto.ApiResponse;
import com.example.pet_care_booking.dto.CartDTO;
import com.example.pet_care_booking.dto.CartItemDTO;
import com.example.pet_care_booking.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/cart")
@RequiredArgsConstructor
public class CartController {

   private final CartService cartService;

   @GetMapping("")
   public ApiResponse<CartDTO> getCartUser() {
      String email = SecurityContextHolder.getContext().getAuthentication().getName();
      ApiResponse<CartDTO> apiResponse = new ApiResponse<>();
      apiResponse.setData(cartService.getCartByUser(email));

      return apiResponse;
   }

   @PostMapping("/add")
   public ApiResponse<CartDTO> addCart(@RequestBody CartItemDTO cartItemDTO) {
      String email = SecurityContextHolder.getContext().getAuthentication().getName();
      ApiResponse<CartDTO> apiResponse = new ApiResponse<>();
      cartService.addCart(email, cartItemDTO);
      apiResponse.setMessage("Đã thêm sản phẩm vào giỏ hàng");
      return apiResponse;
   }

   @PutMapping("/update")
   public ApiResponse<CartDTO> updateCart(@RequestBody CartItemDTO cartItemDTO) {
      String email = SecurityContextHolder.getContext().getAuthentication().getName();
      ApiResponse<CartDTO> apiResponse = new ApiResponse<>();
      cartService.updateCartByUser(email, cartItemDTO);
      apiResponse.setMessage("Đã sửa sản phẩm trong giỏ hàng");
      return apiResponse;
   }

   @DeleteMapping("/delete")
   public ApiResponse<CartDTO> deleteCart() {
      String email = SecurityContextHolder.getContext().getAuthentication().getName();
      ApiResponse<CartDTO> apiResponse = new ApiResponse<>();
      cartService.deleteCartByUser(email);
      apiResponse.setMessage("Đã xóa tất cả sản phẩm giỏ hàng");
      return apiResponse;
   }

   @DeleteMapping("/delete/{cartItemId}")
   public ApiResponse<CartDTO> deleteCartItem(@PathVariable Long cartItemId) {
      String email = SecurityContextHolder.getContext().getAuthentication().getName();
      ApiResponse<CartDTO> apiResponse = new ApiResponse<>();
      cartService.deleteCartItem(email, cartItemId);
      apiResponse.setMessage("Đã xóa sản phẩm giỏ hàng");
      return apiResponse;
   }


}

