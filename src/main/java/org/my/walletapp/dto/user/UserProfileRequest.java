package org.my.walletapp.dto.user;

import jakarta.validation.constraints.*;

public record UserProfileRequest(

        @NotBlank(message = "Name cannot be empty")
        @Pattern(
                regexp = "^[\\p{L}\\-\\s'’]{1,255}$",
                message = "Name can only contain letters, spaces, hyphens, and apostrophes"
        )
        String name,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        @Size(max = 255, message = "Email is too long")
        String email,

        @Pattern(
                regexp = "^[a-z]{2}([-_][A-Z]{2})?$",
                message = "Invalid locale format (expected format like 'uk', 'uk-UA', 'en-US')"
        )
        String locale,

        @NotNull(message = "Timezone cannot be null")
        String timezone
) {}
