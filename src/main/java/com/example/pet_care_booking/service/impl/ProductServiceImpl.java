package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.request.ProductRequest;
import com.example.pet_care_booking.dto.response.ProductResponse;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.mapper.ProductMapper;
import com.example.pet_care_booking.modal.Categories;
import com.example.pet_care_booking.modal.Images;
import com.example.pet_care_booking.modal.Product;
import com.example.pet_care_booking.modal.enums.ProductStatus;
import com.example.pet_care_booking.repository.CategoriesRepository;
import com.example.pet_care_booking.repository.ProductRepository;
import com.example.pet_care_booking.service.ImageService;
import com.example.pet_care_booking.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

   private final ProductRepository productRepository;
   private final ProductMapper productMapper;
   private final CategoriesRepository productCategoriesRepository;
   private final ImageService imageService;
   private final CategoriesRepository categoriesRepository;

   @Override
   public Page<ProductResponse> getAllProducts(int page, int size) {
      Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
      Page<Product> products = productRepository.findAll(pageable);

      return getProduct(products);
   }

   @Override
   public ProductResponse addProduct(Long id, ProductRequest productRequest, MultipartFile[] image) {
      if (productRepository.existsByNamePro(productRequest.getNamePro())) {
         throw new AppException(ErrorCode.PRODUCT_NAME_EXISTED);
      }

      Categories categories = categoriesRepository.findById(id)
             .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
      try {
         Product product = productMapper.toCreateProduct(productRequest);
         product.setCategory(categories);
         product.setCreatedAt(LocalDateTime.now());
         product.setUpdatedAt(LocalDateTime.now());

         List<Images> imagesList = imageService.uploadProduct(image, productRequest.getNamePro(), product);

         String url = imagesList.get(0).getImageUrl();
         product.setImageUrl(url);
         product.setImages(imagesList);
         product.setStatus(ProductStatus.AVAILABLE);
         Product savedProduct = productRepository.save(product);
         return productMapper.productResponse(savedProduct);
      } catch (IOException e) {
         throw new AppException(ErrorCode.UPDATE_IMAGE_FAIL);
      }

   }

   @Override
   public ProductResponse updateProduct(Long id, ProductRequest productRequest, MultipartFile[] image) {

      Product product = productRepository.findById(id)
             .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

      productRepository.findByNamePro(productRequest.getNamePro())
             .filter(pro -> pro.getId() != id)
             .ifPresent(pro -> {
                throw new AppException(ErrorCode.PRODUCT_NAME_EXISTED);
             });

      try {
         Product product1 = productMapper.toUpdateProduct(id, productRequest);
         if (image != null && image.length > 0) {
            imageService.deleteOldImages(product);
            List<Images> newImages = imageService.uploadProduct(image, productRequest.getNamePro(), product);

            String url = newImages.get(0).getImageUrl();
            product1.setImageUrl(url);

            product1.getImages().clear();
            product1.getImages().addAll(newImages);
         } else {
            product1.setImages(product.getImages());
            product1.setImageUrl(product.getImageUrl());
         }

         if (productRequest.getStatus() != null) {
            product1.setStatus(productRequest.getStatus());
         } else {
            product1.setStatus(product.getStatus());
         }
         product1.setCategory(product.getCategory());
         product1.setCategory(product.getCategory());
         product1.setCreatedAt(product.getCreatedAt());
         product1.setUpdatedAt(LocalDateTime.now());
         Product product2 = productRepository.save(product1);
         return productMapper.productResponse(product2);

      } catch (IOException e) {
         throw new AppException(ErrorCode.UPDATE_IMAGE_FAIL);
      }
   }

   @Override
   public void deleteProduct(Long id) {

      Product product = productRepository.findById(id)
             .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
      try {
         imageService.deleteOldImages(product);
         productRepository.delete(product);
      } catch (IOException e) {
         throw new AppException(ErrorCode.DELETE_IMAGE_FAIL);
      }

   }

   @Override
   public Page<ProductResponse> searchProductByCateId(Long cateId, int page, int size) {
      Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
      Page<Product> products = productRepository.findProductsByCategoryId(cateId, pageable);

      return getProduct(products);
   }

   @Override
   public Page<ProductResponse> searchProductByName(String productName, int page, int size) {
      Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
      Page<Product> products = productRepository.findByNamePro(productName, pageable);

      return getProduct(products);
   }

   @Override
   public ProductResponse getProductById(Long id) {
      Product product = productRepository.findById(id)
             .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

      return productMapper.productResponse(product);
   }

   private Page<ProductResponse> getProduct(Page<Product> products) {
      return products.map(
             product -> ProductResponse.builder()
                    .id(product.getId())
                    .namePro(product.getNamePro())
                    .imageUrl(product.getImageUrl())
                    .price(product.getPrice())
                    .description(product.getDescription())
                    .createdAt(product.getCreatedAt())
                    .updatedAt(product.getUpdatedAt())
                    .images(product.getImages())
                    .build()
      );
   }
}
