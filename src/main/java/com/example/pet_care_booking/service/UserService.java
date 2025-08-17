package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.request.user.UserCreateRequest;
import com.example.pet_care_booking.dto.request.user.UserUpdateRequest;
import com.example.pet_care_booking.dto.response.user.UserResponse;
import org.springframework.data.domain.Page;

public interface UserService {
   Page<UserResponse> getAllUsers(int page, int size);
   UserResponse addUser(UserCreateRequest userCreateRequest);
   UserResponse updateUser(Long id,UserUpdateRequest userUpdateRequest);
   void deleteUser(Long id);
   Page<UserResponse> getUser(String key, int page, int size);
}
