package org.my.walletapp.dto.user;

import java.time.ZoneId;

public record UserProfileRequest(
        String name,
        String email,
        String locale,
        ZoneId timezone
) {}
