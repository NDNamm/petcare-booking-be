package com.example.pet_care_booking.controller;

import com.example.pet_care_booking.dto.ApiResponse;
import com.example.pet_care_booking.dto.RoleDTO;
import com.example.pet_care_booking.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/role")
public class RoleController {
   private final RoleService roleService;

   @GetMapping("")
   public ApiResponse<List<RoleDTO>> getAllRoles() {
      ApiResponse<List<RoleDTO>> apiResponse = new ApiResponse<>();
      apiResponse.setData(roleService.select());
      return apiResponse;
   }
}
