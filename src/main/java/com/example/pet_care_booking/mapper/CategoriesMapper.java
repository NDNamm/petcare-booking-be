package com.example.pet_care_booking.mapper;

import com.example.pet_care_booking.dto.request.category.CategoriesRequest;
import com.example.pet_care_booking.dto.response.category.CategoriesResponse;
import com.example.pet_care_booking.modal.Categories;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = false))
public interface CategoriesMapper {

   CategoriesResponse toCategories(Categories categories);

   @Mapping(target = "id", ignore = true)
   @Mapping(target = "imageUrl", ignore = true)
   @Mapping(target = "createdAt", ignore = true)
   @Mapping(target = "updatedAt", ignore = true)
   Categories addCategory(CategoriesRequest add);

   @Mapping(target = "imageUrl", ignore = true)
   @Mapping(target = "updatedAt", ignore = true)
   Categories updateCategory(@MappingTarget Categories categories, CategoriesRequest update);
}
