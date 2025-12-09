package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.CategoriesDTO;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.modal.Categories;
import com.example.pet_care_booking.repository.CategoriesRepository;
import com.example.pet_care_booking.service.CategoriesService;
import com.example.pet_care_booking.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {

   private final CategoriesRepository categoriesRepository;
   private final ImageService imageService;

   @Override
   public Page<CategoriesDTO> getAllCate(String name, int page, int size) {
      Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
      Page<Categories> pageCate;
      if (name == null) {
         pageCate = categoriesRepository.findAll(pageable);
      } else {
         pageCate = categoriesRepository.findByNameCateContainingIgnoreCase(name, pageable);
      }

      return getCategoryResponse(pageCate);
   }

   @Override
   public CategoriesDTO addCate(CategoriesDTO categoriesDTO, MultipartFile image) {
      boolean categories = categoriesRepository.existsByNameCate(categoriesDTO.getNameCate());
      if (categories) {
         throw new RuntimeException("Category already exists");
      }
      try {
         Categories category = Categories.builder()
                .nameCate(categoriesDTO.getNameCate())
                .imageUrl(imageService.uploadCate(image))
                .description(categoriesDTO.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
         Categories saved = categoriesRepository.save(category);

         return convertToDTO(saved);
      } catch (IOException e) {
         throw new AppException(ErrorCode.UPDATE_IMAGE_FAIL);
      }
   }

   @Override
   public CategoriesDTO updateCate(Long id, CategoriesDTO categoriesDTO, MultipartFile image) {
      Categories categories = categoriesRepository.findById(id)
             .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

      categoriesRepository.findByNameCate(categories.getNameCate())
             .filter(cate -> !cate.getId().equals(id))
             .ifPresent(cate -> {
                throw new AppException(ErrorCode.CATEGORY_NAME_EXISTED);
             });

      try {
         if (image != null && !image.isEmpty()) {
            String url = imageService.uploadCate(image);
            categories.setImageUrl(url);
         }
         categories.setNameCate(categoriesDTO.getNameCate());
         categories.setDescription(categoriesDTO.getDescription());
         categories.setUpdatedAt(LocalDateTime.now());
         Categories saved = categoriesRepository.save(categories);

         return convertToDTO(saved);
      } catch (IOException e) {
         throw new AppException(ErrorCode.UPDATE_IMAGE_FAIL);
      }

   }

   @Override
   public void deleteCate(Long id) {
      Categories categories = categoriesRepository.findById(id)
             .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

      categoriesRepository.delete(categories);
   }

   private Page<CategoriesDTO> getCategoryResponse(Page<Categories> categories) {
      return categories.map(cate -> CategoriesDTO.builder()
             .id(cate.getId())
             .nameCate(cate.getNameCate())
             .imageUrl(cate.getImageUrl())
             .description(cate.getDescription())
             .createdAt(cate.getCreatedAt().toString())
             .updatedAt(cate.getUpdatedAt().toString())
             .build());
   }

   private CategoriesDTO convertToDTO(Categories categories) {
      return CategoriesDTO.builder()
             .id(categories.getId())
             .nameCate(categories.getNameCate())
             .imageUrl(categories.getImageUrl())
             .description(categories.getDescription())
              .createdAt(categories.getCreatedAt().toString())
              .updatedAt(categories.getUpdatedAt().toString())
             .build();
   }
}
