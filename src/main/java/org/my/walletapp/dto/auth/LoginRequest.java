package org.my.walletapp.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest (

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        @Size(max = 255)
        String email,

        @NotBlank(message = "Password cannot be empty")
        String password
) {}
