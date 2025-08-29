package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.VeterinariansDTO;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.modal.Veterinarians;
import com.example.pet_care_booking.repository.VeterinarianRepository;
import com.example.pet_care_booking.service.VeterinarianService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VeterinarianServiceImpl implements VeterinarianService {

   private final VeterinarianRepository veterinarianRepository;

   @Override
   public Page<VeterinariansDTO> getVeterinarians(String name, String phoneNumber, String email, int page, int size) {
      Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
      Page<Veterinarians> vets;

      if (name == null && phoneNumber == null && email == null) {
         vets = veterinarianRepository.findAll(pageable);
      } else {
         vets = veterinarianRepository.searchVet(name, phoneNumber, email, pageable);
      }

      return vets.map(
             vet -> VeterinariansDTO.builder()
                    .id(vet.getId())
                    .name(vet.getName())
                    .phoneNumber(vet.getPhoneNumber())
                    .email(vet.getEmail())
                    .createdAt(vet.getCreatedAt())
                    .updatedAt(vet.getUpdatedAt())
                    .build()
      );
   }

   @Override
   public VeterinariansDTO addVet(VeterinariansDTO vet) {
      if (veterinarianRepository.existsByEmailAndPhoneNumber(vet.getEmail(), vet.getPhoneNumber())) {
         throw new AppException(ErrorCode.VET_EXIST);
      }

      Veterinarians vets = Veterinarians.builder()
             .name(vet.getName())
             .phoneNumber(vet.getPhoneNumber())
             .email(vet.getEmail())
             .createdAt(LocalDateTime.now())
             .updatedAt(LocalDateTime.now())
             .build();
      veterinarianRepository.save(vets);

      return getVet(vets);
   }

   @Override
   public VeterinariansDTO updateVet(Long id, VeterinariansDTO vet) {
      Veterinarians vets = veterinarianRepository.findById(id)
             .orElseThrow(() -> new AppException(ErrorCode.VET_NOT_FOUND));

      if (veterinarianRepository.existsByEmailAndPhoneNumber(vet.getEmail(), vet.getPhoneNumber())
             && !vets.getId().equals(id)) {
         throw new AppException(ErrorCode.VET_EXIST);
      }

      vets.setName(vet.getName());
      vets.setPhoneNumber(vet.getPhoneNumber());
      vets.setEmail(vet.getEmail());
      vets.setUpdatedAt(LocalDateTime.now());
      veterinarianRepository.save(vets);

      return getVet(vets);
   }

   @Override
   public void deleteVet(Long id) {
      Veterinarians vets = veterinarianRepository.findById(id)
             .orElseThrow(() -> new AppException(ErrorCode.VET_NOT_FOUND));

      veterinarianRepository.delete(vets);
   }

   private VeterinariansDTO getVet(Veterinarians vet) {
      return VeterinariansDTO.builder()
             .id(vet.getId())
             .name(vet.getName())
             .phoneNumber(vet.getPhoneNumber())
             .email(vet.getEmail())
             .createdAt(vet.getCreatedAt())
             .updatedAt(vet.getUpdatedAt())
             .build();
   }
}
