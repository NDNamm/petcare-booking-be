package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.request.category.CategoriesRequest;
import com.example.pet_care_booking.dto.response.category.CategoriesResponse;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.mapper.CategoriesMapper;
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
   private final CategoriesMapper categoriesMapper;

   @Override
   public Page<CategoriesResponse> getAllCate(int page, int size) {
      Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
      Page<Categories> pageCate = categoriesRepository.findAll(pageable);

      return getCategoryResponse(pageCate);
   }

   @Override
   public CategoriesResponse addCate(CategoriesRequest categoriesRequest, MultipartFile image) {
      boolean categories = categoriesRepository.existsByNameCate(categoriesRequest.getNameCate());
      if (categories) {
         throw new AppException(ErrorCode.CATEGORY_NAME_EXISTED);
      }
      try {
         Categories category = categoriesMapper.addCategory(categoriesRequest);
         category.setImageUrl(imageService.uploadCate(image));
         category.setCreatedAt(LocalDateTime.now());
         category.setUpdatedAt(LocalDateTime.now());

         // Save and get the saved entity with generated ID
         Categories savedCategory = categoriesRepository.save(category);

         return categoriesMapper.toCategories(savedCategory);
      } catch (IOException e) {
         throw new AppException(ErrorCode.UPDATE_IMAGE_FAIL);
      }
   }

   @Override
   public CategoriesResponse updateCate(Long id, CategoriesRequest categoriesRequest, MultipartFile image) {
      Categories categories = categoriesRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

      categoriesRepository.findByNameCate(categoriesRequest.getNameCate())
            .filter(cate -> cate.getId() != id)
            .ifPresent(cate -> {
               throw new AppException(ErrorCode.CATEGORY_NAME_EXISTED);
            });

      try {
         categories = categoriesMapper.updateCategory(categories, categoriesRequest);

         if (image != null && !image.isEmpty()) {
            String url = imageService.uploadCate(image);
            categories.setImageUrl(url);
         }

         // Save and get the updated entity
         Categories updatedCategory = categoriesRepository.save(categories);

         return categoriesMapper.toCategories(updatedCategory);

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

   @Override
   public Page<CategoriesResponse> selectCategoryByName(String cateName, int page, int size) {
      Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
      Page<Categories> pageCate = categoriesRepository.searchCategoriesByName(cateName, pageable);
      if (pageCate.isEmpty()) {
         throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
      }

      return getCategoryResponse(pageCate);
   }

   private Page<CategoriesResponse> getCategoryResponse(Page<Categories> categories) {
      return categories.map(cate -> CategoriesResponse.builder()
            .id(cate.getId())
            .nameCate(cate.getNameCate())
            .imageUrl(cate.getImageUrl())
            .description(cate.getDescription())
            .createdAt(cate.getCreatedAt())
            .updatedAt(cate.getUpdatedAt())
            .build());
   }
}
