package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.RoleDTO;
import com.example.pet_care_booking.dto.UserDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
   Page<UserDTO> getAllUsers(String name, String email, String phoneNumber,int page, int size);
   void addUser(UserDTO userDTO);
   void updateRoleUser(Long id,UserDTO userDTO);
   void deleteUser(Long id);

}
