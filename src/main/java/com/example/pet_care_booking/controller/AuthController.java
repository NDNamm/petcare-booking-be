package com.example.pet_care_booking.controller;

import com.example.pet_care_booking.dto.ApiResponse;
import com.example.pet_care_booking.dto.AuthDTO;
import com.example.pet_care_booking.dto.UserDTO;
import com.example.pet_care_booking.security.JwtUtils;
import com.example.pet_care_booking.service.AuthService;
import com.example.pet_care_booking.service.impl.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final TokenBlacklistService  tokenBlacklistService;
    @PostMapping("/login")
    public ApiResponse<AuthDTO> login(@RequestBody UserDTO user,
                                      HttpServletResponse res) {
        ApiResponse<AuthDTO> response = new ApiResponse<>();
        response.setData(authService.login(user, res));
        return response;
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody UserDTO user) {
        ApiResponse<Void> response = new ApiResponse<>();
        authService.register(user);
        response.setMessage("Đăng kí tài khoản thành công");
        return response;
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletResponse res, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        ApiResponse<Void> response = new ApiResponse<>();
        authService.logout(res);
        String token = authHeader.substring(7);
        long remainingMillis = jwtUtils.getRemainingMillis(token);
        tokenBlacklistService.addToBlackList(token, remainingMillis);
        response.setMessage("Đăng xuất tài khoản thành công");
        return response;
    }

    @PostMapping("/refresh-token")
    public ApiResponse<AuthDTO> refresh(HttpServletRequest req, HttpServletResponse res) {
        ApiResponse<AuthDTO> response = new ApiResponse<>();
        response.setData(authService.refresh(req, res));
        return response;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status((HttpStatus.UNAUTHORIZED)).body("Unauthorized");
        }
        String username = authentication.getName();
        String role = authentication.getAuthorities()
                .stream().findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("USER");

        return ResponseEntity.ok(Map.of(
                "username", username,
                "role", role
        ));
    }
}
