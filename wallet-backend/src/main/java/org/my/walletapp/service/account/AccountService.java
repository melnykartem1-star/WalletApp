package org.my.walletapp.service.account;

import org.my.walletapp.dto.account.AccountRequest;
import org.my.walletapp.dto.account.AccountResponse;

import java.util.List;

public interface AccountService{
    List<AccountResponse> getAllAccounts(Long userId);
    AccountResponse createAccount(Long userId, AccountRequest request);
    AccountResponse updateAccountById(Long userId, Long accountId, AccountRequest request);
    AccountResponse getAccountById(Long userId, Long accountId);
    void deleteAccountById(Long userId, Long accountId);
}
