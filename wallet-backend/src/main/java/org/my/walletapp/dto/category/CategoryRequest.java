package org.my.walletapp.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.my.walletapp.enums.CategoryType;

public record CategoryRequest(

        @NotBlank(message = "Title cannot be empty")
        @Size(max = 255, message = "Title is too long")
        String title,

        @Size(max = 10_000, message = "Description is too long")
        String description,

        @NotNull(message = "Type cannot be empty")
        CategoryType type,

        @Size(max = 255, message = "Color length is too long")
        String color,

        @Size(max = 255, message = "Icon length is too long")
        String icon
) {}
