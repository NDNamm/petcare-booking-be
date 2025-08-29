package com.example.pet_care_booking.dto;

import com.example.pet_care_booking.modal.enums.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;

    @NotBlank(message = "PRODUCT_NAME_INVALID")
    @Size(min = 2, message = "PRODUCT_NAME_INVALID")
    private String namePro;
    private String slug;
    private String imageUrl;
    @NotBlank(message = "PRODUCT_PRICE_INVALID")
    private BigDecimal price;
    private Long sl;
    @NotBlank(message = "PRODUCT_STATUS_INVALID")
    private ProductStatus status;
    private String description;
    private String createdAt;
    private String updatedAt;
    private List<ImagesDTO> imagesDTO;
    private Long categoryId;
    private String categoryName;
    private Double averageRating;
    private List<VariantDTO> variants;
}
