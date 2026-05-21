package org.my.walletapp.dto.user;

import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        String name,
        String email,
        String locale,
        String timezone,
        LocalDateTime createdAt,
        LocalDateTime lastLogon
) {}
