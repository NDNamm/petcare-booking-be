package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.UserDTO;
import org.springframework.data.domain.Page;

public interface UserService {
   Page<UserDTO> getAllUsers(int page, int size);
   void addUser(UserDTO userDTO);
   void updateUser(Long id,UserDTO userDTO);
   void deleteUser(Long id);
   Page<UserDTO> getUser(String key, int page, int size);
}
