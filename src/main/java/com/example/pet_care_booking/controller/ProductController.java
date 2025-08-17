package com.example.pet_care_booking.controller;


import com.example.pet_care_booking.dto.request.ProductRequest;
import com.example.pet_care_booking.dto.response.ApiResponse;
import com.example.pet_care_booking.dto.response.ProductResponse;
import com.example.pet_care_booking.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

   private final ProductService productService;

   @GetMapping("")
   public ApiResponse<Page<ProductResponse>> getAllProduct(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "6") int size) {

      ApiResponse<Page<ProductResponse>> apiResponse = new ApiResponse<>();
      apiResponse.setData(productService.getAllProducts(page, size));
      return apiResponse;
   }

   @PostMapping(value = "/add/{cateId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public ApiResponse<ProductResponse> addProduct(@RequestPart("productRequest") String productJson,
                                             @RequestPart("image") MultipartFile[] image,
                                             @PathVariable Long cateId) {
      ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
      try {
         ObjectMapper mapper = new ObjectMapper();
         ProductRequest request = mapper.readValue(productJson, ProductRequest.class);

         apiResponse.setData(productService.addProduct(cateId, request, image));
         apiResponse.setMessage("Thêm product thành công");
         return apiResponse;
      } catch (Exception e) {
         apiResponse.setMessage("Error: " + e.getMessage());
         return apiResponse;
      }
   }

   @PutMapping("/update/{id}")
   public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id,
                                                @RequestPart("productRequest") String productJson,
                                                @RequestPart(value = "image", required = false) MultipartFile[] image) {
      ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
      try {
         ObjectMapper mapper = new ObjectMapper();
         ProductRequest request = mapper.readValue(productJson,ProductRequest.class);

         apiResponse.setData(productService.updateProduct(id, request, image ));
         apiResponse.setMessage("Update product thành công");
         return apiResponse;
      } catch (Exception e) {
         apiResponse.setMessage("Error: " + e.getMessage());
         return apiResponse;
      }
   }

   @DeleteMapping("/delete/{id}")
   public ApiResponse<ProductResponse> deleteProduct(@PathVariable Long id) {

      ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
      productService.deleteProduct(id);
      apiResponse.setMessage("Delete product thành công");
      return apiResponse;

   }

   @GetMapping("/search/{name}")
   public ApiResponse<Page<ProductResponse>> selectProduct(@PathVariable String name,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "6") int size) {

      ApiResponse<Page<ProductResponse>> apiResponse = new ApiResponse<>();
      apiResponse.setData(productService.searchProductByName(name, page, size));
      return apiResponse;
   }
   @GetMapping("/select/{cateId}")
   public ApiResponse<Page<ProductResponse>> selectProductByCateId(@PathVariable Long cateId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "6") int size) {

      ApiResponse<Page<ProductResponse>> apiResponse = new ApiResponse<>();
      apiResponse.setData(productService.searchProductByCateId(cateId,page, size));
      return apiResponse;
   }

   @GetMapping("{productId}")
   public ApiResponse<ProductResponse> getProduct(@PathVariable Long productId) {
      ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
      apiResponse.setData(productService.getProductById(productId));
      return apiResponse;
   }




}
