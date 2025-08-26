package com.example.pet_care_booking.service;

import com.example.pet_care_booking.dto.VeterinariansDTO;
import org.springframework.data.domain.Page;

public interface VeterinarianService {
   Page<VeterinariansDTO> getVeterinarians(String name, String phoneNumber, String email,int page, int size);
   VeterinariansDTO addVet(VeterinariansDTO vet);
   VeterinariansDTO updateVet(Long id,VeterinariansDTO vet);
   void deleteVet(Long id);
}
