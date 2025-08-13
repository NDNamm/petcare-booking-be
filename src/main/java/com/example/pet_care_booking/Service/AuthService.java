package com.example.pet_care_booking.Service;

import com.example.pet_care_booking.dto.request.LoginRequest;
import com.example.pet_care_booking.dto.request.RegisterRequest;
import com.example.pet_care_booking.dto.response.LoginResponse;
import com.example.pet_care_booking.dto.response.RegisterResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
   LoginResponse login(LoginRequest loginRequest, HttpServletResponse response);
   void logout(HttpServletResponse response);
   RegisterResponse register(RegisterRequest registerRequest);
   LoginResponse refresh(HttpServletRequest request, HttpServletResponse response);
}
