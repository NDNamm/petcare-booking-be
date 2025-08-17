package com.example.pet_care_booking.mapper;

import com.example.pet_care_booking.dto.request.auth.RegisterRequest;
import com.example.pet_care_booking.modal.User;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = false))
public interface AuthMapper {
   @Mapping(target = "id", ignore = true)
   @Mapping(target = "role", ignore = true)
   @Mapping(target = "password", ignore = true)
   @Mapping(target = "createdAt", ignore = true)
   @Mapping(target = "updatedAt", ignore = true)
   User toRegister(RegisterRequest request);
}
