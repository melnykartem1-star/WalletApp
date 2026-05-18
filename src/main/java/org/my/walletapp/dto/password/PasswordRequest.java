package org.my.walletapp.dto.password;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordRequest(

        @NotBlank(message = "Password cannot be empty")
        String oldPassword,

        @NotBlank(message = "Password cannot be empty")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,128}$",
                message = "Password must be 8-128 characters long and include at least one letter, one number, and one special character"
        )
        String newPassword
) {}
