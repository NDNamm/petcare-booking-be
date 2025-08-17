package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.request.category.CategoriesRequest;
import com.example.pet_care_booking.dto.response.category.CategoriesResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface CategoriesService {
   Page<CategoriesResponse> getAllCate(int page, int size);
   CategoriesResponse addCate(CategoriesRequest categoriesRequest, MultipartFile image);
   CategoriesResponse updateCate(Long id, CategoriesRequest categoriesRequest, MultipartFile image);
   void deleteCate(Long id);
   Page<CategoriesResponse> selectCategoryByName(String cateName, int page, int size);
}
