package org.my.walletapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserProfilePatchRequest(
        @Pattern(regexp = "^[\\p{L}\\-\\s'’]{1,255}$", message = "Invalid name format")
        String name,

        @Email(message = "Invalid email format")
        @Size(max = 255)
        String email,

        @Pattern(regexp = "^[a-z]{2}([-_][A-Z]{2})?$", message = "Invalid locale format")
        String locale,

        String timezone
) {}
