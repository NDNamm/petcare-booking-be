package com.example.pet_care_booking.mapper;

import com.example.pet_care_booking.dto.request.ProductRequest;
import com.example.pet_care_booking.dto.response.ProductResponse;
import com.example.pet_care_booking.modal.Product;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = false))
public interface ProductMapper {

   ProductResponse productResponse(Product product);

   @Mapping(target = "id", ignore = true)
   @Mapping(target = "imageUrl", ignore = true)
   @Mapping(target = "status", ignore = true)
   @Mapping(target = "createdAt", ignore = true)
   @Mapping(target = "updatedAt", ignore = true)
   @Mapping(target = "images", ignore = true)
   Product toCreateProduct(ProductRequest productRequest);

   @Mapping(target = "imageUrl", ignore = true)
   @Mapping(target = "updatedAt", ignore = true)
   @Mapping(target = "status", ignore = true)
   @Mapping(target = "images", ignore = true)
   Product toUpdateProduct(Long id,ProductRequest productRequest);
}
