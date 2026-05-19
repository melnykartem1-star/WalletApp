package org.my.walletapp.dto.user;

import java.time.LocalDate;
import java.time.ZoneId;

public record UserProfileResponse(
        Long id,
        String name,
        String email,
        String locale,
        ZoneId timezone,
        LocalDate createdAt,
        LocalDate lastLogon
) {}
