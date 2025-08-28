package com.example.pet_care_booking.controller;

import com.example.pet_care_booking.dto.ApiResponse;
import com.example.pet_care_booking.dto.ExaminationDTO;
import com.example.pet_care_booking.service.ExaminationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/examination")
public class ExaminationController {

   private final ExaminationService examinationService;

   @GetMapping("")
   public ApiResponse<List<ExaminationDTO>> getAllUsers() {
      ApiResponse<List<ExaminationDTO>> apiResponse = new ApiResponse<>();
      apiResponse.setData(examinationService.getAllExaminations());
      return apiResponse;
   }

   @PostMapping("/create")
   public ApiResponse<ExaminationDTO> createUser(@Valid @RequestBody ExaminationDTO examinationDTO) {
      ApiResponse<ExaminationDTO> apiResponse = new ApiResponse<>();
      apiResponse.setData(examinationService.createExamination(examinationDTO));
      apiResponse.setMessage("Successfully created Examination");
      return apiResponse;
   }

   @PutMapping("/update/{id}")
   public ApiResponse<ExaminationDTO> updateUser(@PathVariable Long id,
                                          @RequestBody ExaminationDTO examinationDTO) {
      ApiResponse<ExaminationDTO> apiResponse = new ApiResponse<>();
      apiResponse.setData(examinationService.updateExamination(id, examinationDTO));
      apiResponse.setMessage("Update Examination thành cong");
      return apiResponse;
   }

   @DeleteMapping("/delete/{userId}")
   public ApiResponse<Void> deleteUser(@PathVariable Long userId) {
      ApiResponse<Void> apiResponse = new ApiResponse<>();
      examinationService.deleteExamination(userId);
      apiResponse.setMessage("Deleted Examination thành cong");
      return apiResponse;
   }

}
