package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.RatingDTO;
import org.springframework.data.domain.Page;

public interface RatingService {
   RatingDTO createRating(Long productId,RatingDTO ratingDTO, String userName);
   Page<RatingDTO> getAllRatings(Long productId, int page, int size);
   RatingDTO updateRating(RatingDTO ratingDTO, Long productId, String userName);
   void deleteRating(Long productId,String requesterId, String userName);
}
