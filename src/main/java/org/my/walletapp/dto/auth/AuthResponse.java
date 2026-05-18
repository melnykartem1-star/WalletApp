package org.my.walletapp.dto.auth;

public record AuthResponse(
        Long id,
        String name,
        String email,
        String accessToken,
        String refreshToken
) {}
