package org.my.walletapp.service.auth;

import org.my.walletapp.dto.auth.AuthResponse;
import org.my.walletapp.dto.auth.LoginRequest;
import org.my.walletapp.dto.auth.RegisterRequest;
import org.my.walletapp.dto.token.RefreshTokenRequest;
import org.my.walletapp.dto.token.RefreshTokenResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    RefreshTokenResponse refresh(RefreshTokenRequest request);
}
