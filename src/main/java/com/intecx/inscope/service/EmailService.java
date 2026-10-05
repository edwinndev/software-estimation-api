package com.intecx.inscope.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otpCode);
    void sendWelcomeEmail(String toEmail, String firstName);
    void sendInvitationOtpEmail(String toEmail, String firstName, String otpCode);
}
