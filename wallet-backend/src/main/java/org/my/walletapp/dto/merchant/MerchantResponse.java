package org.my.walletapp.dto.merchant;

import org.my.walletapp.dto.category.CategoryResponse;

public record MerchantResponse(
        Long id,
        CategoryResponse category,
        String name,
        String icon,
        boolean isActive
) {}
