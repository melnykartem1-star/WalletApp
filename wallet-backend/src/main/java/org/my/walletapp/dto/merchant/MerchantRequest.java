package org.my.walletapp.dto.merchant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MerchantRequest(

        Long categoryId,

        @NotBlank(message = "Name cannot be empty")
        @Size(max = 255, message = "Name is too long")
        String name,

        @Size(max = 255, message = "Icon is too long")
        String icon
) {}
