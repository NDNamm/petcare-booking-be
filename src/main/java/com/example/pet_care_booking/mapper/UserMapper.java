package com.example.pet_care_booking.mapper;

import com.example.pet_care_booking.dto.request.user.UserCreateRequest;
import com.example.pet_care_booking.dto.request.user.UserUpdateRequest;
import com.example.pet_care_booking.dto.response.user.UserResponse;
import com.example.pet_care_booking.modal.User;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = false))
public interface UserMapper {
   @Mapping(target = "password", ignore = true)
   UserResponse toUserResponse(User user);

   @Mapping(target = "id", ignore = true)
   @Mapping(target = "role", ignore = true)
   @Mapping(target = "password", ignore = true)
   @Mapping(target = "createdAt", ignore = true)
   @Mapping(target = "updatedAt", ignore = true)
   User toCreateUser(UserCreateRequest userCreateRequest);

   @Mapping(target = "id", ignore = true)
   @Mapping(target = "role", ignore = true)
   @Mapping(target = "password", ignore = true)
   @Mapping(target = "createdAt", ignore = true)
   @Mapping(target = "updatedAt", ignore = true)
   User toUpdateUser(@MappingTarget User user, UserUpdateRequest request);

}
