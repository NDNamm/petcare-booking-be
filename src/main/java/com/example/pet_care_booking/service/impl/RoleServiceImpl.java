package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.RoleDTO;
import com.example.pet_care_booking.modal.Role;
import com.example.pet_care_booking.repository.RoleRepository;
import com.example.pet_care_booking.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
   private final RoleRepository roleRepository;
   @Override
   public List<RoleDTO> select() {
      List<Role> roles = roleRepository.findAll();
      return roles.stream()
             .map(role -> RoleDTO.builder()
                    .id(role.getId())
                    .name(role.getName())
                    .build()
             )
             .toList();
   }
}
