package org.my.walletapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.my.walletapp.dto.merchant.MerchantRequest;
import org.my.walletapp.dto.merchant.MerchantResponse;
import org.my.walletapp.entity.Merchant;

@Mapper(
        componentModel = "spring",
        uses = {CategoryMapper.class}, // Обов'язково для MerchantResponse
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MerchantMapper {

    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(target = "id", ignore = true)
    Merchant toEntity(MerchantRequest request);

    MerchantResponse toResponse(Merchant merchant);

    @Mapping(source = "categoryId", target = "category.id")
    Merchant partialUpdate(MerchantRequest request, @MappingTarget Merchant merchant);
}