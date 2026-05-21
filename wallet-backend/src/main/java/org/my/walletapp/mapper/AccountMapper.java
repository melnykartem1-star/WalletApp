package org.my.walletapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.my.walletapp.dto.account.AccountRequest;
import org.my.walletapp.dto.account.AccountResponse;
import org.my.walletapp.entity.Account;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AccountMapper {
    Account toEntity(AccountRequest request);
    AccountResponse toResponse(Account account);
    Account partialUpdate(AccountRequest request, @MappingTarget Account account);
}
