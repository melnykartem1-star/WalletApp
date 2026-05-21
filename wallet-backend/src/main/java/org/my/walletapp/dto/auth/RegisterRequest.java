package org.my.walletapp.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

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

        @NotBlank(message = "Password cannot be empty")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,128}$",
                message = "Password must be 8-128 characters long and include at least one letter, one number, and one special character"
        )
        String password
) {}
