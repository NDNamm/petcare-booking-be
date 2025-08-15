package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.service.AuthService;
import com.example.pet_care_booking.dto.request.LoginRequest;
import com.example.pet_care_booking.dto.request.RegisterRequest;
import com.example.pet_care_booking.dto.response.LoginResponse;
import com.example.pet_care_booking.dto.response.RegisterResponse;
import com.example.pet_care_booking.modal.Role;
import com.example.pet_care_booking.modal.User;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.mapper.AuthMapper;
import com.example.pet_care_booking.repository.RoleRepository;
import com.example.pet_care_booking.repository.UserRepository;
import com.example.pet_care_booking.security.JwtUtils;
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
   private final AuthMapper authMapper;

   @Override
   public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {
      User user = userRepository.findUserByUserName(loginRequest.getUserName())
             .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

      if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
         String accessToken = jwtUtils.createAccessToken(user.getUserName(), user.getRole().getName());
         String refreshToken = jwtUtils.createRefreshToken(user.getUserName());
         Cookie cookie = new Cookie("refresh_token", refreshToken);
         cookie.setHttpOnly(true);
         cookie.setPath("/");
         cookie.setSecure(true);
         cookie.setMaxAge(7 * 24 * 60 * 60);
         response.addCookie(cookie);

         return new LoginResponse(
                user.getUserName(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getRole().getName(),
                accessToken
         );
      } else {
         throw new UsernameNotFoundException("The username or password is incorrect");
      }

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
   public RegisterResponse register(RegisterRequest registerRequest) {
      Role role = roleRepository.findByName("USER")
             .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
      Optional<User> existUsername = userRepository.findUserByUserName(registerRequest.getUserName());
      if (existUsername.isPresent()) throw new AppException(ErrorCode.USER_NAME_EXIST);
      if (userRepository.findUserByEmail(registerRequest.getEmail()).isPresent()) {
         throw new AppException(ErrorCode.EMAIL_EXISTED);
      }

      if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
         throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
      }
      if (userRepository.findUserByPhoneNumber(registerRequest.getPhoneNumber()).isPresent()) {
         throw new AppException(ErrorCode.PHONE_EXISTED);
      }
      String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
      User user = authMapper.toRegister(registerRequest);
      user.setPassword(encodedPassword);
      user.setRole(role);
      user.setCreatedAt(LocalDateTime.now());
      user.setUpdatedAt(LocalDateTime.now());
      userRepository.save(user);
      return new RegisterResponse(
             user.getUserName(),
             user.getPhoneNumber(),
             user.getEmail(),
             user.getRole().getName()
      );
   }

   @Override
   public LoginResponse refresh(HttpServletRequest request, HttpServletResponse response) {
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

      return new LoginResponse(
             user.getUserName(),
             user.getEmail(),
             user.getPhoneNumber(),
             user.getRole().getName(),
             newAccessToken
      );
   }

}
