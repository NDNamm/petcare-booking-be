package com.example.pet_care_booking.controller;

import com.example.pet_care_booking.Service.AuthService;
import com.example.pet_care_booking.dto.request.LoginRequest;
import com.example.pet_care_booking.dto.request.RegisterRequest;
import com.example.pet_care_booking.dto.response.ApiResponse;
import com.example.pet_care_booking.dto.response.LoginResponse;
import com.example.pet_care_booking.dto.response.RegisterResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

   private final AuthService authService;

   @PostMapping("/login")
   public ApiResponse<LoginResponse> login(@RequestBody LoginRequest user,
                                           HttpServletResponse res) {
      ApiResponse<LoginResponse> response = new ApiResponse<>();
      response.setData(authService.login(user, res));
      return response;
   }

   @PostMapping("/register")
   public ApiResponse<RegisterResponse> register(@RequestBody RegisterRequest user) {
      ApiResponse<RegisterResponse> response = new ApiResponse<>();
      response.setData(authService.register(user));
      response.setMessage("Đăng kí tài khoản thành công");
      return response;
   }

   @PostMapping("/logout")
   public ApiResponse<Void> logout(HttpServletResponse res) {
      ApiResponse<Void> response = new ApiResponse<>();
      authService.logout(res);
      response.setMessage("Đăng xuất tài khoản thành công");
      return response;
   }

   @PostMapping("/refresh-token")
   public ApiResponse<LoginResponse> refresh(HttpServletRequest req, HttpServletResponse res) {
      ApiResponse<LoginResponse> response = new ApiResponse<>();
      response.setData(authService.refresh(req, res));
      return response;
   }
}
