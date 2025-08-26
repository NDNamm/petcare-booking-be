package com.example.pet_care_booking.controller;

import com.example.pet_care_booking.dto.ApiResponse;
import com.example.pet_care_booking.dto.VeterinariansDTO;
import com.example.pet_care_booking.service.VeterinarianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vet")
public class VeterinarianController {

   private final VeterinarianService veterinarianService;

   @GetMapping("")
   public ApiResponse<Page<VeterinariansDTO>> getAllUsers(@RequestParam(required = false) String name,
                                                          @RequestParam(required = false) String phoneNumber,
                                                          @RequestParam(required = false) String email,
                                                          @RequestParam(defaultValue = "0", required = false) int page,
                                                          @RequestParam(defaultValue = "5") int size) {
      ApiResponse<Page<VeterinariansDTO>> apiResponse = new ApiResponse<>();
      apiResponse.setData(veterinarianService.getVeterinarians(name, phoneNumber, email, page, size));
      return apiResponse;
   }

   @PostMapping("/create")
   public ApiResponse<VeterinariansDTO> createUser(@Valid @RequestBody VeterinariansDTO vet) {
      ApiResponse<VeterinariansDTO> apiResponse = new ApiResponse<>();
      apiResponse.setData(veterinarianService.addVet(vet));
      apiResponse.setMessage("Successfully created veterinarian");
      return apiResponse;
   }

   @PutMapping("/update/{id}")
   public ApiResponse<VeterinariansDTO> updateUser(@PathVariable Long id,
                                          @RequestBody VeterinariansDTO vet) {
      ApiResponse<VeterinariansDTO> apiResponse = new ApiResponse<>();
      apiResponse.setData(veterinarianService.updateVet(id,vet));
      apiResponse.setMessage("Update veterinarian thành cong");
      return apiResponse;
   }

   @DeleteMapping("/delete/{id}")
   public ApiResponse<Void> deleteUser(@PathVariable Long id) {
      ApiResponse<Void> apiResponse = new ApiResponse<>();
      veterinarianService.deleteVet(id);
      apiResponse.setMessage("Deleted veterinarian thành cong");
      return apiResponse;
   }
}
