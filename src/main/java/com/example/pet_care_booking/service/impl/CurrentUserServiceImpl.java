package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.modal.User;
import com.example.pet_care_booking.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserServiceImpl {
    private final UserRepository userRepository;

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findUserByUserName(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy user"));
    }
}
