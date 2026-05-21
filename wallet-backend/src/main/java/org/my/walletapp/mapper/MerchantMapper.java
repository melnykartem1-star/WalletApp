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
        uses = {CategoryMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MerchantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    Merchant toEntity(MerchantRequest request);

    MerchantResponse toResponse(Merchant merchant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    Merchant partialUpdate(MerchantRequest request, @MappingTarget Merchant merchant);
}