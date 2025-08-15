package com.example.pet_care_booking.mapper;

import com.example.pet_care_booking.dto.request.RegisterRequest;
import com.example.pet_care_booking.entity.RoleEntity;
import com.example.pet_care_booking.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuthMapper {
    public UserEntity toEntity(RegisterRequest registerRequest, RoleEntity role, String encodedPassword) {
        return UserEntity.builder()
                .username(registerRequest.getUserName())
                .role(role)
                .password(encodedPassword)
                .createdAt(LocalDateTime.now())
                .email(registerRequest.getEmail())
                .phoneNumber(registerRequest.getPhoneNumber())
                .build();
    }
}
