package org.my.walletapp.dto.token;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {}
