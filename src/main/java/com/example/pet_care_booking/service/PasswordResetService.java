package com.example.pet_care_booking.service;

import jakarta.mail.MessagingException;

public interface PasswordResetService {
    void forgotPassword(String email) throws MessagingException;
    void resetPassword(String token, String newPassword);
}
