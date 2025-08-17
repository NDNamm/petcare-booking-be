package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.request.auth.LoginRequest;
import com.example.pet_care_booking.dto.request.auth.RegisterRequest;
import com.example.pet_care_booking.dto.response.auth.LoginResponse;
import com.example.pet_care_booking.dto.response.auth.RegisterResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
   LoginResponse login(LoginRequest loginRequest, HttpServletResponse response);
   void logout(HttpServletResponse response);
   RegisterResponse register(RegisterRequest registerRequest);
   LoginResponse refresh(HttpServletRequest request, HttpServletResponse response);
}
