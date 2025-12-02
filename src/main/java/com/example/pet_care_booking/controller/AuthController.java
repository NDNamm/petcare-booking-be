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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ApiResponse<AuthDTO> login(@RequestBody UserDTO user,
                                      HttpServletResponse res) {
        ApiResponse<AuthDTO> response = new ApiResponse<>();
        response.setData(authService.login(user, res));
        return response;
    }

    @PostMapping("/register")
    public ApiResponse<Void> register( @RequestBody UserDTO user) {
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

    @GetMapping("/getInfoUser")
    public ResponseEntity<AuthDTO> getInfoUser() {
        return ResponseEntity.ok(authService.getInforUser());
    }

    @PutMapping("/reset-password/{userId}")
    public ResponseEntity<?> resetPassword(@PathVariable Long userId){
        authService.resetPassword(userId);
        return ResponseEntity.ok().build();
    }
}
