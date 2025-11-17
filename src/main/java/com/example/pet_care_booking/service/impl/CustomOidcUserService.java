package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.modal.Role;
import com.example.pet_care_booking.modal.User;
import com.example.pet_care_booking.repository.RoleRepository;
import com.example.pet_care_booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser user = new OidcUserService().loadUser(userRequest);
        String email = user.getAttribute("email");
        String username = email.split("@")[0];
        Role role = roleRepository.findById(2L)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        String phone = UUID.randomUUID().toString();
        User user2 = userRepository.findUserByEmail(email)
                .orElseGet(() -> User.builder()
                        .email(email)
                        .createdAt(LocalDateTime.now())
                        .userName(username)
                        .phoneNumber(phone.substring(0,10))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .password(UUID.randomUUID().toString())
                        .provider("google")
                        .role(role)
                        .build());
        userRepository.save(user2);
        return user;
    }
}
