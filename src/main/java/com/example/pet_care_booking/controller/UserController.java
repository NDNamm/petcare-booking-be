package com.example.pet_care_booking.controller;

import com.example.pet_care_booking.dto.ApiResponse;
import com.example.pet_care_booking.dto.UserDTO;
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
   public ApiResponse<Page<UserDTO>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "5") int size) {
      ApiResponse<Page<UserDTO>> apiResponse = new ApiResponse<>();
      apiResponse.setData(userService.getAllUsers(page, size));
      return apiResponse;
   }

   @PostMapping("/create")
   public ApiResponse<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
      ApiResponse<UserDTO> apiResponse = new ApiResponse<>();
      userService.addUser(userDTO);
      apiResponse.setMessage("Successfully created user");
      return apiResponse;
   }

   @PutMapping("/update/{userId}")
   public ApiResponse<UserDTO> updateUser(@PathVariable Long userId,
                                        @Valid  @RequestBody UserDTO updateUserDTO) {
      ApiResponse<UserDTO> apiResponse = new ApiResponse<>();
      userService.updateUser(userId,updateUserDTO);
      apiResponse.setMessage("Update user thành cong");
      return apiResponse;
   }
}
