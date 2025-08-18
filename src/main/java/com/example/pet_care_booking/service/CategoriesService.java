package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.CategoriesDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface CategoriesService {

   Page<CategoriesDTO> getAllCate(String name, int page, int size);

   CategoriesDTO addCate(CategoriesDTO categoriesDTO, MultipartFile image);

   CategoriesDTO updateCate(Long id, CategoriesDTO categoriesDTO, MultipartFile image);

   void deleteCate(Long id);

}
