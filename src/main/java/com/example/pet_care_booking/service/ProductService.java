package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.request.ProductRequest;
import com.example.pet_care_booking.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
   Page<ProductResponse> getAllProducts(int page, int size);
   ProductResponse addProduct(Long id,ProductRequest productRequest, MultipartFile[] image) throws IOException;
   ProductResponse updateProduct(Long id,ProductRequest productRequest, MultipartFile[] image);
   void deleteProduct(Long id);
   Page<ProductResponse> searchProductByCateId(Long cateId, int page, int size);
   Page<ProductResponse> searchProductByName(String productName, int page, int size);
   ProductResponse getProductById(Long id);
}
