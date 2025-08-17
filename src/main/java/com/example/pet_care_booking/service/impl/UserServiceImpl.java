package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.request.user.UserCreateRequest;
import com.example.pet_care_booking.dto.request.user.UserUpdateRequest;
import com.example.pet_care_booking.dto.response.user.UserResponse;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.mapper.UserMapper;
import com.example.pet_care_booking.modal.Role;
import com.example.pet_care_booking.modal.User;
import com.example.pet_care_booking.repository.RoleRepository;
import com.example.pet_care_booking.repository.UserRepository;
import com.example.pet_care_booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
   private final UserRepository userRepository;
   private final RoleRepository roleRepository;
   private final PasswordEncoder passwordEncoder;
   private final UserMapper userMapper;

   @Override
   public Page<UserResponse> getAllUsers(int page, int size) {
      Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
      Page<User> users = userRepository.findAll(pageable);

      return users.map(user -> UserResponse.builder()
             .id(user.getId())
             .userName(user.getUserName())
             .email(user.getEmail())
             .phoneNumber(user.getPhoneNumber())
             .password(user.getPassword())
             .createdAt(user.getCreatedAt())
             .updatedAt(user.getUpdatedAt())
             .nameRole(user.getRole().getName())
             .build());
   }

   @Override
   public UserResponse addUser(UserCreateRequest userCreateRequest) {
      Role role = roleRepository.findByName("USER")
             .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
      Optional<User> existUsername = userRepository.findUserByUserName(userCreateRequest.getUserName());
      if (existUsername.isPresent()) throw new AppException(ErrorCode.USER_NAME_EXIST);
      if (userRepository.findUserByEmail(userCreateRequest.getEmail()).isPresent()) {
         throw new AppException(ErrorCode.EMAIL_EXISTED);
      }

      if (!userCreateRequest.getPassword().equals(userCreateRequest.getConfirmPassword())) {
         throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
      }
      if (userRepository.findUserByPhoneNumber(userCreateRequest.getPhoneNumber()).isPresent()) {
         throw new AppException(ErrorCode.PHONE_EXISTED);
      }
      String encodedPassword = passwordEncoder.encode(userCreateRequest.getPassword());
      User user = userMapper.toCreateUser(userCreateRequest);
      user.setPassword(encodedPassword);
      user.setRole(role);
      user.setCreatedAt(LocalDateTime.now());
      user.setUpdatedAt(LocalDateTime.now());
      userRepository.save(user);

      return userMapper.toUserResponse(user);
   }

   @Override
   public UserResponse updateUser(Long id,UserUpdateRequest userUpdateRequest) {
      User user = userRepository.findById(id)
             .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

      user = userMapper.toUpdateUser(user, userUpdateRequest);
      userRepository.save(user);
      return userMapper.toUserResponse(user);
   }

   @Override
   public void deleteUser(Long id) {
      User user = userRepository.findById(id)
             .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

      userRepository.delete(user);
   }

   @Override
   public Page<UserResponse> getUser(String key, int page, int size) {
      return null;
   }
}
