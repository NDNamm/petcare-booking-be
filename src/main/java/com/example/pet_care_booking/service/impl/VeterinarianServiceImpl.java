package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.VeterinariansDTO;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.modal.Role;
import com.example.pet_care_booking.modal.User;
import com.example.pet_care_booking.modal.Veterinarians;
import com.example.pet_care_booking.repository.RoleRepository;
import com.example.pet_care_booking.repository.UserRepository;
import com.example.pet_care_booking.repository.VeterinarianRepository;
import com.example.pet_care_booking.service.VeterinarianService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VeterinarianServiceImpl implements VeterinarianService {

    private final VeterinarianRepository veterinarianRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
    @Transactional
    public VeterinariansDTO addVet(VeterinariansDTO vet) {
        if (veterinarianRepository.existsByEmailAndPhoneNumber(vet.getEmail(), vet.getPhoneNumber())) {
            throw new AppException(ErrorCode.VET_EXIST);
        }
        if (userRepository.findUserByEmail(vet.getEmail()).isPresent()) {
            throw new RuntimeException("Account already exists!");
        }
        Role role = roleRepository.findById(3L)
                .orElseThrow(() -> new RuntimeException("Role not found!"));
        String usernameSplit = vet.getEmail().split("@")[0];
        User user = new User();
        user.setRole(role);
        user.setEmail(vet.getEmail());
        user.setPhoneNumber(vet.getPhoneNumber());
        user.setUserName(usernameSplit);
        user.setPassword(bCryptPasswordEncoder.encode("12345678"));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        Veterinarians vets = new Veterinarians();
        vets.setName(vet.getName());
        vets.setEmail(vet.getEmail());
        vets.setPhoneNumber(vet.getPhoneNumber());
        vets.setCreatedAt(LocalDateTime.now());
        vets.setUpdatedAt(LocalDateTime.now());
        vets.setUser(user);

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
