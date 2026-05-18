package org.my.walletapp.dto.password;

public record PasswordRequest(
        String oldPassword,
        String newPassword
) {}
