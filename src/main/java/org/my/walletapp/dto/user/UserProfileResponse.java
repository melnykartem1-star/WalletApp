package org.my.walletapp.dto.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public record UserProfileResponse(
        Long id,
        String name,
        String email,
        String locale,
        ZoneId timezone,
        LocalDateTime createdAt,
        LocalDateTime lastLogon
) {}
