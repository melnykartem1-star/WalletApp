package org.my.walletapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.my.walletapp.dto.transaction.TransactionRequest;
import org.my.walletapp.dto.transaction.TransactionResponse;
import org.my.walletapp.entity.Transaction;

@Mapper(
        componentModel = "spring",
        uses = {CategoryMapper.class, MerchantMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TransactionMapper {

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "merchant", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "targetAccount", ignore = true)
    Transaction toEntity(TransactionRequest request);

    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "targetAccount.id", target = "targetAccountId")
    @Mapping(source = "account.currency", target = "currency")
    TransactionResponse toResponse(Transaction transaction);

}