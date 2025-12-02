package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.AuthDTO;
import com.example.pet_care_booking.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
   AuthDTO login(UserDTO dto, HttpServletResponse response);
   void logout(HttpServletResponse response);
   void register(UserDTO dto);
   AuthDTO refresh(HttpServletRequest request, HttpServletResponse response);
    AuthDTO getInforUser();
    void resetPassword(Long userId);}
