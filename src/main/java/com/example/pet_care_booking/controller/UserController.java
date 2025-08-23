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
   public ApiResponse<Page<UserDTO>> getAllUsers(@RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String phoneNumber,
                                                 @RequestParam(required = false) String email,
                                                 @RequestParam(defaultValue = "0", required = false) int page,
                                                 @RequestParam(defaultValue = "5") int size) {
      ApiResponse<Page<UserDTO>> apiResponse = new ApiResponse<>();
      apiResponse.setData(userService.getAllUsers(name, phoneNumber, email, page, size));
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
                                          @RequestBody UserDTO updateUserDTO) {
      ApiResponse<UserDTO> apiResponse = new ApiResponse<>();
      userService.updateRoleUser(userId, updateUserDTO);
      apiResponse.setMessage("Update user thành cong");
      return apiResponse;
   }

   @DeleteMapping("/delete/{userId}")
   public ApiResponse<Void> deleteUser(@PathVariable Long userId) {
      ApiResponse<Void> apiResponse = new ApiResponse<>();
      userService.deleteUser(userId);
      apiResponse.setMessage("Deleted user thành cong");
      return apiResponse;
   }

}
