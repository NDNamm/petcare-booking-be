package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.dto.AuthDTO;
import com.example.pet_care_booking.dto.UserDTO;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.modal.Role;
import com.example.pet_care_booking.modal.User;
import com.example.pet_care_booking.repository.RoleRepository;
import com.example.pet_care_booking.repository.UserRepository;
import com.example.pet_care_booking.security.JwtUtils;
import com.example.pet_care_booking.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepository;

    @Override
    public AuthDTO login(UserDTO dto, HttpServletResponse response) {
        User user = userRepository.findUserByUserName(dto.getUserName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            String accessToken = jwtUtils.createAccessToken(user.getUserName(), user.getRole().getName());
            String refreshToken = jwtUtils.createRefreshToken(user.getUserName());
            Cookie cookie = new Cookie("refresh_token", refreshToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setSecure(true);
            cookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(cookie);
            return getAuth(user, accessToken);
        } else {
            throw new UsernameNotFoundException("The username or password is incorrect");
        }

    }

    private AuthDTO getAuth(User user, String accessToken) {
        return new AuthDTO(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole().getName(),
                accessToken
        );
    }

    @Override
    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }


    @Override
    public void register(UserDTO dto) {

        Role role = roleRepository.findById(2L)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        Optional<User> existUsername = userRepository.findUserByUserName(dto.getUserName());
        if (existUsername.isPresent()) throw new AppException(ErrorCode.USER_NAME_EXIST);
        if (userRepository.findUserByEmail(dto.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        if (userRepository.findUserByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        User regis = User.builder()
                .role(role)
                .userName(dto.getUserName())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .password(encodedPassword)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(regis);

    }

    @Override
    public AuthDTO refresh(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if ("refresh_token".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
            }
        }

        if (refreshToken == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String userName = jwtUtils.extractUsername(refreshToken);
        User user = userRepository.findUserByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtUtils.createAccessToken(user.getUserName(), user.getRole().getName());
        return getAuth(user, newAccessToken);
    }


}
