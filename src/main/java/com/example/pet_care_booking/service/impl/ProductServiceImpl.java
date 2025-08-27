package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.ImagesDTO;
import com.example.pet_care_booking.dto.ProductDTO;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.modal.Categories;
import com.example.pet_care_booking.modal.Images;
import com.example.pet_care_booking.modal.Product;
import com.example.pet_care_booking.modal.enums.ProductStatus;
import com.example.pet_care_booking.repository.CategoriesRepository;
import com.example.pet_care_booking.repository.ProductRepository;
import com.example.pet_care_booking.service.ImageService;
import com.example.pet_care_booking.service.ProductService;
import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ImageService imageService;
    private final CategoriesRepository categoriesRepository;

    @Override
    public Page<ProductDTO> getAllProducts(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Product> products;

        if (name == null || name.trim().isEmpty()) {
            products = productRepository.findAll(pageable);
        } else {
            products = productRepository.findByNameProContainingIgnoreCase(name, pageable);
        }

        return getProduct(products);
    }

    @Override
    public ProductDTO addProduct(Long id, ProductDTO dto, MultipartFile[] image) {
        if (productRepository.existsByNamePro(dto.getNamePro())) {
            throw new AppException(ErrorCode.PRODUCT_NAME_EXISTED);
        }
        Slugify slugify = new Slugify();
        String convertSlug = slugify.slugify(dto.getSlug());
        Categories categories = categoriesRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        try {

            Product product = Product.builder()
                    .namePro(dto.getNamePro())
                    .description(dto.getDescription())
                    .price(dto.getPrice())
                    .status(ProductStatus.AVAILABLE)
                    .sl(dto.getSl())
                    .slug(convertSlug)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .category(categories)
                    .build();
            productRepository.save(product);

            List<Images> images = imageService.uploadProduct(image, dto.getNamePro(), product);
            String imageUrl = images.get(0).getImageUrl();

            product.setImageUrl(imageUrl);
            product.setImages(images);
            Product product1 = productRepository.save(product);

            return convertProduct(product1);
        } catch (IOException e) {
            throw new AppException(ErrorCode.UPDATE_IMAGE_FAIL);
        }

    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO dto, MultipartFile[] image) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        productRepository.findByNamePro(dto.getNamePro())
                .filter(pro -> !pro.getId().equals(id))
                .ifPresent(pro -> {
                    throw new AppException(ErrorCode.PRODUCT_NAME_EXISTED);
                });

        try {
            product.setNamePro(dto.getNamePro());
            product.setPrice(dto.getPrice());
            product.setDescription(dto.getDescription());
            product.setUpdatedAt(LocalDateTime.now());
            product.setSl(dto.getSl());
            if (dto.getStatus() != null) {
                product.setStatus(dto.getStatus());
            }
            // Nếu có ảnh mới thì mới xóa ảnh cũ và upload ảnh mới
            if (image != null && image.length > 0) {
                imageService.deleteOldImages(product);
                // Upload ảnh mới
                List<Images> newImages = imageService.uploadProduct(image, dto.getNamePro(), product);
                product.getImages().clear();
                product.getImages().addAll(newImages);
                product.setImageUrl(newImages.get(0).getImageUrl());
            }

            if (product.getSl() <= 0) {
                product.setStatus(ProductStatus.OUT_OF_STOCK);
            }
            Product product1 = productRepository.save(product);
            return convertProduct(product1);
        } catch (IOException e) {
            throw new AppException(ErrorCode.UPDATE_IMAGE_FAIL);
        }
    }

    @Override
    public ProductDTO findBySlug(String slug) {
        Product product = productRepository.findBySlug(slug);
        return convertProduct(product);
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
    public Page<ProductDTO> searchProductByCateId(Long cateId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Product> products = productRepository.findProductsByCategoryId(cateId, pageable);

        return getProduct(products);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        return convertProduct(product);
    }

    private Page<ProductDTO> getProduct(Page<Product> products) {
        return products.map(product -> {
            List<ImagesDTO> imageDTOs = toImageDTOs(product.getImages());
            return ProductDTO.builder()
                    .id(product.getId())
                    .namePro(product.getNamePro())
                    .price(product.getPrice())
                    .description(product.getDescription())
                    .status(product.getStatus())
                    .slug(product.getSlug())
                    .sl(product.getSl())
                    .averageRating(product.getAverageRating())
                    .createdAt(product.getCreatedAt().toString())
                    .updatedAt(product.getUpdatedAt().toString())
                    .averageRating(product.getAverageRating())
                    .imageUrl(imageDTOs.stream().findFirst().map(ImagesDTO::getImageUrl).orElse(null))
                    .imagesDTO(imageDTOs)
                    .categoryId(product.getCategory().getId())
                    .build();
        });
    }

    private List<ImagesDTO> toImageDTOs(List<Images> images) {
        return Optional.ofNullable(images).orElse(List.of())
                .stream()
                .map(img -> ImagesDTO.builder()
                        .id(img.getId())
                        .publicId(img.getPublicId())
                        .imageUrl(img.getImageUrl())
                        .size(img.getSize())
                        .build())
                .toList();
    }

    private ProductDTO convertProduct(Product product) {
        List<ImagesDTO> imageDTOs = toImageDTOs(product.getImages());
        return ProductDTO.builder()
                .id(product.getId())
                .namePro(product.getNamePro())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .sl(product.getSl())
                .categoryName(product.getCategory().getNameCate())
                .description(product.getDescription())
                .status(product.getStatus())
                .averageRating(product.getAverageRating())
                .createdAt(product.getCreatedAt().toString())
                .updatedAt(product.getUpdatedAt().toString())
                .imagesDTO(imageDTOs)
                .build();
    }
}
