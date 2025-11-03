package com.example.pet_care_booking.controller;

import com.example.pet_care_booking.dto.ProductReviewDTO;
import com.example.pet_care_booking.enums.ReviewStatus;
import com.example.pet_care_booking.service.ProductReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/product-review")
@RequiredArgsConstructor
public class ProductReviewController {
    private final ProductReviewService productReviewService;

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getMyReviews(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "phoneNumber", required = false) String phoneNumber
    ) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<ProductReviewDTO> reviews = productReviewService.findAll(phoneNumber, pageRequest);
        Map<String, Object> response = new HashMap<>();
        response.put("reviews", reviews);
        response.put("totalPage", reviews.getTotalPages());
        response.put("page", page);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getMyReviewsDetails(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "slug") String slug
    ) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<ProductReviewDTO> reviews = productReviewService.findByProductSlug(slug, pageRequest);
        Map<String, Object> response = new HashMap<>();
        response.put("reviews", reviews);
        response.put("totalPage", reviews.getTotalPages());
        response.put("page", page);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/create-review")
    public ResponseEntity<String> createReview(
            @RequestBody ProductReviewDTO reviewDTO

    ) {
        productReviewService.createReview(reviewDTO);
        return ResponseEntity.ok("Review created successfully, pending approval.");
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateReviewStatus(
            @PathVariable Long id,
            @RequestParam ReviewStatus status
    ) {
        productReviewService.updateReviewStatus(id, status);
        return ResponseEntity.ok("Status updated");
    }


}
