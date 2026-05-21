package org.my.walletapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.my.walletapp.dto.transaction.TransferRequest;
import org.my.walletapp.dto.transaction.TransferResponse;
import org.my.walletapp.entity.Transaction;

@Mapper(
        componentModel = "spring",
        uses = {CategoryMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TransferMapper {
    @Mapping(source = "accountId", target = "account.id")
    @Mapping(source = "targetAccountId", target = "targetAccount.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Transaction toEntity(TransferRequest request);

    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "targetAccount.id", target = "targetAccountId")
    TransferResponse toResponse(Transaction transaction);
}
