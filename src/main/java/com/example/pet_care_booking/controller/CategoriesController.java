package com.example.pet_care_booking.controller;

import com.example.pet_care_booking.dto.request.category.CategoriesRequest;
import com.example.pet_care_booking.dto.response.ApiResponse;
import com.example.pet_care_booking.dto.response.category.CategoriesResponse;
import com.example.pet_care_booking.service.CategoriesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoriesController {

   private final CategoriesService categoriesService;

   @GetMapping("")
   public ApiResponse<Page<CategoriesResponse>> getAllCategory(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "5") int size) {

      ApiResponse<Page<CategoriesResponse>> response = new ApiResponse<>();
      response.setData(categoriesService.getAllCate(page, size));
      return response;
   }

   @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public ApiResponse<CategoriesResponse> addCategory(@RequestPart("categoryRequest") String categoriesJson,
                                                      @RequestPart("image") MultipartFile image) {
      ApiResponse<CategoriesResponse> response = new ApiResponse<>();
      try {

         ObjectMapper mapper = new ObjectMapper();
         CategoriesRequest request = mapper.readValue(categoriesJson, CategoriesRequest.class);

         response.setData(categoriesService.addCate(request, image));
         response.setMessage("Thêm category thành công");
         return response;
      } catch (Exception e) {
         response.setCode(9999);
         response.setMessage("Thêm category thất bại: " + e.getMessage());
         return response;
      }
   }

   @PutMapping("/update/{id}")
   public ApiResponse<CategoriesResponse> updateCategory(@PathVariable Long id,
                                                         @RequestPart("categoryRequest") String categoriesJson,
                                                         @RequestPart(value = "image", required = false) MultipartFile image) {
      ApiResponse<CategoriesResponse> response = new ApiResponse<>();
      try {
         ObjectMapper mapper = new ObjectMapper();
         CategoriesRequest request = mapper.readValue(categoriesJson, CategoriesRequest.class);

         response.setData(categoriesService.updateCate(id, request, image));
         response.setMessage("Update category thành công");
         return response;
      } catch (Exception e) {
         response.setCode(9999);
         response.setMessage("Update category thất bại: " + e.getMessage());
         return response;
      }
   }

   @DeleteMapping("/delete/{id}")
   public ApiResponse<CategoriesResponse> deleteCategory(@PathVariable Long id) {

      ApiResponse<CategoriesResponse> response = new ApiResponse<>();
      categoriesService.deleteCate(id);
      response.setMessage("Delete category thành công");
      return response;
   }

   @GetMapping("/search/{name}")
   public ApiResponse<Page<CategoriesResponse>> selectCategory(@PathVariable String name,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size) {

      ApiResponse<Page<CategoriesResponse>> response = new ApiResponse<>();
      response.setData(categoriesService.selectCategoryByName(name, page, size));
      return response;
   }
}
