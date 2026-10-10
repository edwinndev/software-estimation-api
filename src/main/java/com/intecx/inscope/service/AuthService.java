package com.intecx.inscope.service;

import com.intecx.inscope.dto.request.auth.ChangePasswordRequest;
import com.intecx.inscope.dto.request.auth.LoginRequest;
import com.intecx.inscope.dto.request.auth.RefreshTokenRequest;
import com.intecx.inscope.dto.request.auth.RequestPasswordResetRequest;
import com.intecx.inscope.dto.request.auth.ResetPasswordRequest;
import com.intecx.inscope.dto.request.auth.UpdateProfileRequest;
import com.intecx.inscope.dto.request.auth.VerifyResetCodeRequest;
import com.intecx.inscope.dto.response.AuthResponse;
import com.intecx.inscope.dto.response.MessageResponse;
import com.intecx.inscope.dto.response.UserResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    MessageResponse logout(String bearerToken);
    UserResponse getCurrentProfile(String email);
    UserResponse updateCurrentProfile(String currentEmail, UpdateProfileRequest request);
    MessageResponse changePassword(String currentEmail, ChangePasswordRequest request);
    MessageResponse requestPasswordReset(RequestPasswordResetRequest request);
    MessageResponse verifyResetCode(VerifyResetCodeRequest request);
    MessageResponse resetPassword(ResetPasswordRequest request);
}
