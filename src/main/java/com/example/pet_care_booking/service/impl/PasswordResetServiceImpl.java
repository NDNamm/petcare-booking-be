package com.example.pet_care_booking.service.impl;

import com.example.pet_care_booking.exception.OAuth2PasswordResetException;
import com.example.pet_care_booking.modal.PasswordResetToken;
import com.example.pet_care_booking.modal.User;
import com.example.pet_care_booking.repository.PasswordResetTokenRepository;
import com.example.pet_care_booking.repository.UserRepository;
import com.example.pet_care_booking.service.PasswordResetService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;

    @Override
    public void forgotPassword(String email) throws MessagingException {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if ("google".equals(user.getProvider())) {
            throw new OAuth2PasswordResetException("Cannot reset password for Google account");
        }
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();
        passwordResetTokenRepository.save(passwordResetToken);
        String resetUrl = "http://localhost:5173/reset-password?token=" + token;

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        String htmlMsg = "<!DOCTYPE html>"
                + "<html><body style='font-family:Arial,sans-serif;'>"
                + "<h2>Password Reset Request</h2>"
                + "<p>Hello,</p>"
                + "<p>We received a request to reset your password.</p>"
                + "<p>Click the button below to reset your password:</p>"
                + "<a href='" + resetUrl + "' style='display:inline-block;padding:10px 20px;"
                + "font-size:16px;color:white;background-color:#0d9488;text-decoration:none;"
                + "border-radius:5px;'>Reset Password</a>"
                + "<p>If you did not request a password reset, please ignore this email.</p>"
                + "<p>Thanks,<br/>Your Company Team</p>"
                + "</body></html>";

        helper.setTo(user.getEmail());
        helper.setSubject("Password Reset Request");
        helper.setText(htmlMsg, true);
        helper.setFrom("noreply@yourcompany.com");
        javaMailSender.send(message);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if(passwordResetToken.isEmpty()){
            throw new EntityNotFoundException("Not found account with token : " + token);
        }
        if (passwordResetToken.get().isExpired() || passwordResetToken.get().isUsed()) {
            throw new IllegalArgumentException("Token expired or already used");
        }
        User user = passwordResetToken.get().getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        passwordResetToken.get().setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken.get());
    }
}
