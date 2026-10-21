package com.intecx.inscope.service;

public interface EmailService {

    void sendOtpEmail(String toEmail, String otpCode, int expirationMinutes);

    void sendInvitationOtpEmail(String toEmail, String firstName, String otpCode, int expirationHours);

    void sendWelcomeEmail(String toEmail, String firstName);
}
