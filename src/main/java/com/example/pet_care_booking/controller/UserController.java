package com.example.pet_care_booking.controller;

import com.example.pet_care_booking.dto.request.user.UserCreateRequest;
import com.example.pet_care_booking.dto.request.user.UserUpdateRequest;
import com.example.pet_care_booking.dto.response.ApiResponse;
import com.example.pet_care_booking.dto.response.user.UserResponse;
import com.example.pet_care_booking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

   private final UserService userService;


   @GetMapping("")
   ApiResponse<Page<UserResponse>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "5") int size) {
      ApiResponse<Page<UserResponse>> apiResponse = new ApiResponse<>();
      apiResponse.setData(userService.getAllUsers(page, size));
      return apiResponse;
   }

   @PostMapping("/create")
   ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreateRequest userRequest) {
      ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
      apiResponse.setData(userService.addUser(userRequest));
      return apiResponse;
   }

   @PutMapping("/update/{userId}")
   ApiResponse<UserResponse> updateUser(@PathVariable Long userId,
                                        @Valid  @RequestBody UserUpdateRequest userUpdateRequest) {
      ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
      apiResponse.setData(userService.updateUser(userId,userUpdateRequest));
      return apiResponse;
   }
}
