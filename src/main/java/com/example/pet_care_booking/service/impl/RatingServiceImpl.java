package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.RatingDTO;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.modal.Product;
import com.example.pet_care_booking.modal.Rating;
import com.example.pet_care_booking.modal.User;
import com.example.pet_care_booking.repository.ProductRepository;
import com.example.pet_care_booking.repository.RatingRepository;
import com.example.pet_care_booking.repository.UserRepository;
import com.example.pet_care_booking.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

   private final RatingRepository ratingRepository;
   private final UserRepository userRepository;
   private final ProductRepository productRepository;

   @Override
   public RatingDTO createRating(Long productId, RatingDTO ratingDTO, String userName) {
      User user = userRepository.findUserByUserName(userName)
             .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

      Product product = productRepository.findById(productId)
             .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

      Rating rating = Rating.builder()
             .ratingValue(ratingDTO.getRatingValue())
             .comment(ratingDTO.getComment())
             .createdAt(LocalDateTime.now())
             .updateAt(LocalDateTime.now())
             .product(product)
             .user(user)
             .build();

      ratingRepository.save(rating);
      getAverageRating(productId);
      return getRating(rating);
   }

   @Override
   public Page<RatingDTO> getAllRatings(Long productId, int page, int size) {

      Product product = productRepository.findById(productId)
             .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

      Pageable pageable = PageRequest.of(page, size, Sort.by("updateAt").descending());
      Page<Rating> ratings = ratingRepository.findRatingByProduct(product, pageable);

      return ratings.map(rating -> RatingDTO.builder()
             .ratingValue(rating.getRatingValue())
             .comment(rating.getComment())
             .updatedAt(rating.getUpdateAt())
             .userName(rating.getUser().getUserName())
             .namePro(rating.getProduct().getNamePro())
             .build());
   }

   @Override
   public RatingDTO updateRating(RatingDTO ratingDTO, Long productId, String userName) {
      User user = userRepository.findUserByUserName(userName)
             .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

      Product product = productRepository.findById(productId)
             .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

      Rating rating = ratingRepository.findRatingByProductIdAndUserId(productId, user.getId())
             .orElseThrow(() -> new AppException(ErrorCode.RATING_NOT_FOUND));


      if(ratingDTO.getRatingValue() != null){
         rating.setRatingValue(ratingDTO.getRatingValue());
      }

      if (ratingDTO.getComment() != null) {
         rating.setComment(ratingDTO.getComment());
      }

      rating.setUpdateAt(LocalDateTime.now());
      rating.setProduct(product);
      ratingRepository.save(rating);
      getAverageRating(productId);
      return getRating(rating);
   }

   @Override
   public void deleteRating(Long productId, String requesterId, String userName) {
      User user = userRepository.findUserByUserName(userName)
             .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

      Rating rating = ratingRepository.findRatingByProductIdAndUserId(productId, user.getId())
             .orElseThrow(() -> new AppException(ErrorCode.RATING_NOT_FOUND));

      if (rating.getUser().getId().equals(user.getId()) || user.getRole().getName().equals("ADMIN")) {
         ratingRepository.delete(rating);
         return;
      }

      throw new AppException(ErrorCode.UNAUTHORIZED);
   }


   public void getAverageRating(Long productId) {
      Product product = productRepository.findById(productId)
             .orElseThrow(() -> new RuntimeException("Product Not Found"));

      List<Rating> ratings = ratingRepository.findRatingByProductId(productId);
      if (ratings.isEmpty()) {
         return;
      }

      double sum = 0.0;
      for (Rating rating : ratings) {
         sum += rating.getRatingValue();
      }

      double average = sum / ratings.size();
      average = Math.round(average * 100.0) / 100.0;

      product.setAverageRating(average);
      productRepository.save(product);

   }


   private RatingDTO getRating(Rating rating) {
      return RatingDTO.builder()
             .id(rating.getId())
             .ratingValue(rating.getRatingValue())
             .comment(rating.getComment())
             .createdAt(rating.getCreatedAt())
             .updatedAt(rating.getUpdateAt())
             .namePro(rating.getProduct().getNamePro())
             .userName(rating.getUser().getUserName())
             .build();
   }
}
