package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

public interface ProductService {
   Page<ProductDTO> getAllProducts(String name, Long categoryId, String sizeVariant
           , BigDecimal minPrice, BigDecimal maxPrice, int page, int size);
   ProductDTO addProduct(Long id,ProductDTO productRequest, MultipartFile[] image) throws IOException;
   ProductDTO updateProduct(Long id,ProductDTO productRequest, MultipartFile[] image);
   ProductDTO findBySlug(String slug);
   void deleteProduct(Long id);
//   Page<ProductDTO> searchProductByCateId(Long cateId, int page, int size);
   ProductDTO getProductById(Long id);
}
