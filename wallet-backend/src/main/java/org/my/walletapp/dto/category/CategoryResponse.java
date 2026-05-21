package org.my.walletapp.dto.category;

import org.my.walletapp.enums.CategoryType;

public record CategoryResponse(
        Long id,
        String title,
        String description,
        CategoryType type,
        boolean isActive,
        String color,
        String icon
) {}
