package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.RoleDTO;
import com.example.pet_care_booking.dto.UserDTO;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.modal.Role;
import com.example.pet_care_booking.modal.User;
import com.example.pet_care_booking.modal.Veterinarians;
import com.example.pet_care_booking.repository.RoleRepository;
import com.example.pet_care_booking.repository.UserRepository;
import com.example.pet_care_booking.repository.VeterinarianRepository;
import com.example.pet_care_booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
   private final UserRepository userRepository;
   private final RoleRepository roleRepository;
   private final PasswordEncoder passwordEncoder;
    private final VeterinarianRepository veterinarianRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
   @Override
   public Page<UserDTO> getAllUsers(String name, String email, String phoneNumber, int page, int size) {
      Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
      Page<User> users;

      if (name == null && email == null && phoneNumber == null) {
         users = userRepository.findAll(pageable);
      } else {
         users = userRepository.searchUser(name, email, phoneNumber, pageable);
      }

      return users.map(user -> UserDTO.builder()
             .id(user.getId())
             .userName(user.getUserName())
             .email(user.getEmail())
             .phoneNumber(user.getPhoneNumber())
             .createdAt(user.getCreatedAt().toString())
             .updatedAt(user.getUpdatedAt().toString())
             .role(user.getRole())
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
   public void updateRoleUser(Long id, UserDTO userDTO) {
      User user = userRepository.findById(id)
             .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

      Role role = roleRepository.findById(userDTO.getRole().getId())
             .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

      user.setRole(role);
      userRepository.save(user);
   }

   @Override
   public void deleteUser(Long id) {
      User user = userRepository.findById(id)
             .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
       Veterinarians vet = veterinarianRepository.findByUser(user);
       if(vet != null){
           vet.setUser(null);
           veterinarianRepository.save(vet);

           // Xóa User
           userRepository.delete(user);

           // Xóa Vet sau nếu muốn
           veterinarianRepository.delete(vet);
       }else{
           userRepository.delete(user);
       }
   }

    @Override
    public UserDTO editProfile(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if(!user.getEmail().equals(userDTO.getEmail())){
            Optional<User> checkEmail = userRepository.findUserByEmail(userDTO.getEmail());
            if(checkEmail.isPresent()){
                throw  new AppException(ErrorCode.EMAIL_EXISTED);
            }
            user.setEmail(userDTO.getEmail());
        }
        if(!user.getPhoneNumber().equals(userDTO.getPhoneNumber())){
            Optional<User> checkPhone = userRepository.findUserByPhoneNumber(userDTO.getPhoneNumber());
            if(checkPhone.isPresent()){
                throw  new AppException(ErrorCode.PHONE_EXISTED);
            }

            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if("google".equals(user.getProvider())){
            throw new AppException(ErrorCode.GMAIL_PASSWORD_CHANGE_NOT_ALLOWED);
        }
        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {

            if (!bCryptPasswordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
                throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
            }

            if (!userDTO.getNewPassword().equals(userDTO.getConfirmPassword())) {
                throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
            }

            user.setPassword(bCryptPasswordEncoder.encode(userDTO.getNewPassword()));
        }
        userRepository.save(user);
        return userDTO;
    }


}
