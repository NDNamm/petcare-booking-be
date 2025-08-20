package com.example.pet_care_booking.controller;

import com.example.pet_care_booking.dto.ApiResponse;
import com.example.pet_care_booking.dto.RatingDTO;
import com.example.pet_care_booking.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@RequestMapping("/api/rating")
@RequiredArgsConstructor
public class RatingController {

   private final RatingService ratingService;

   @GetMapping("{productId}")
   public ApiResponse<Page<RatingDTO>> getAllRating(@PathVariable Long productId,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
      ApiResponse<Page<RatingDTO>> apiResponse = new ApiResponse<>();
      apiResponse.setData(ratingService.getAllRatings(productId,page,size));
      return apiResponse;
   }

   @PostMapping("/add/{productId}")
   public ApiResponse<RatingDTO> addRating(@PathVariable Long productId,@RequestBody RatingDTO ratingDTO) {
      String userName = SecurityContextHolder.getContext().getAuthentication().getName();

      ApiResponse<RatingDTO> apiResponse = new ApiResponse<>();
      apiResponse.setData(ratingService.createRating(productId,ratingDTO, userName));
      apiResponse.setMessage("Rating added successfully");
      return apiResponse;
   }

   @PutMapping("update/{productId}")
   public ApiResponse<RatingDTO> updateRating(@PathVariable Long productId,
                                         @RequestBody RatingDTO ratingDTO) {
      String userName = SecurityContextHolder.getContext().getAuthentication().getName();
      ApiResponse<RatingDTO> apiResponse = new ApiResponse<>();

      apiResponse.setData(ratingService.updateRating(ratingDTO, productId, userName));
      apiResponse.setMessage("Rating updated successfully");
      return apiResponse;
   }

   @DeleteMapping("delete/{productId}/{requestID}")
   public ApiResponse<Void> deleteRating(@PathVariable Long productId,
                                         @PathVariable String requestID) {
      String email = SecurityContextHolder.getContext().getAuthentication().getName();
      ApiResponse<Void> apiResponse = new ApiResponse<>();
      ratingService.deleteRating(productId, requestID, email);
      apiResponse.setMessage("Delete rating successfully");
      return apiResponse;
   }


}
