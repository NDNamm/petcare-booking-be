package com.example.pet_care_booking.controller;

import com.example.pet_care_booking.dto.ApiResponse;
import com.example.pet_care_booking.dto.CategoriesDTO;
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
   public ApiResponse<Page<CategoriesDTO>> getAllCategory(@RequestParam(required = false) String name,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size) {

      ApiResponse<Page<CategoriesDTO>> response = new ApiResponse<>();
      response.setData(categoriesService.getAllCate(name, page, size));
      return response;
   }

   @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public ApiResponse<CategoriesDTO> addCategory(@RequestPart("categoriesDTO") String categoriesJson,
                                                 @RequestPart("image") MultipartFile image) {
      ApiResponse<CategoriesDTO> response = new ApiResponse<>();
      try {

         ObjectMapper mapper = new ObjectMapper();
         CategoriesDTO request = mapper.readValue(categoriesJson, CategoriesDTO.class);

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
   public ApiResponse<CategoriesDTO> updateCategory(@PathVariable Long id,
                                                    @RequestPart("categoryDTO") String categoriesJson,
                                                    @RequestPart(value = "image", required = false) MultipartFile image) {
      ApiResponse<CategoriesDTO> response = new ApiResponse<>();
      try {
         ObjectMapper mapper = new ObjectMapper();
         CategoriesDTO request = mapper.readValue(categoriesJson, CategoriesDTO.class);
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
   public ApiResponse<CategoriesDTO> deleteCategory(@PathVariable Long id) {

      ApiResponse<CategoriesDTO> response = new ApiResponse<>();
      categoriesService.deleteCate(id);
      response.setMessage("Delete category thành công");
      return response;
   }

}
