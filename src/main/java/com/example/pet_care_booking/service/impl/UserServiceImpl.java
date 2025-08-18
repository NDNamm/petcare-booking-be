package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.UserDTO;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
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

   @Override
   public Page<UserDTO> getAllUsers(int page, int size) {
      Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
      Page<User> users = userRepository.findAll(pageable);

      return users.map(user -> UserDTO.builder()
             .id(user.getId())
             .userName(user.getUserName())
             .email(user.getEmail())
             .phoneNumber(user.getPhoneNumber())
             .password(user.getPassword())
             .createdAt(user.getCreatedAt())
             .updatedAt(user.getUpdatedAt())
             .roleName(user.getRole().getName())
             .build());
   }

   @Override
   public void addUser(UserDTO userDTO) {
      Role role = roleRepository.findByName("USER")
             .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
      Optional<User> existUsername = userRepository.findUserByUserName(userDTO.getUserName());
      if (existUsername.isPresent()) throw new AppException(ErrorCode.USER_NAME_EXIST);
      if (userRepository.findUserByEmail(userDTO.getEmail()).isPresent()) {
         throw new AppException(ErrorCode.EMAIL_EXISTED);
      }

      if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
         throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
      }
      if (userRepository.findUserByPhoneNumber(userDTO.getPhoneNumber()).isPresent()) {
         throw new AppException(ErrorCode.PHONE_EXISTED);
      }
      User user = User.builder()
             .email(userDTO.getEmail())
             .password(passwordEncoder.encode(userDTO.getPassword()))
             .userName(userDTO.getUserName())
             .phoneNumber(userDTO.getPhoneNumber())
             .createdAt(LocalDateTime.now())
             .updatedAt(LocalDateTime.now())
             .role(role)
             .build();
      userRepository.save(user);
   }

   @Override
   public void updateUser( Long id, UserDTO userDTO) {
      User user = userRepository.findById(id)
             .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

      if(userRepository.findUserByPhoneNumber(userDTO.getPhoneNumber()).isPresent()) {
         throw new AppException(ErrorCode.PHONE_EXISTED);
      }

      if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
         throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
      }
      user.setPhoneNumber(userDTO.getPhoneNumber());
      user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
      user.setUpdatedAt(LocalDateTime.now());
      userRepository.save(user);
   }

   @Override
   public void deleteUser(Long id) {
      User user = userRepository.findById(id)
             .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

      userRepository.delete(user);
   }

   @Override
   public Page<UserDTO> getUser(String key, int page, int size) {
      return null;
   }
}
