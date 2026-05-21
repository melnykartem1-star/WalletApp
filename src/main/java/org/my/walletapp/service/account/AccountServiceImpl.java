package org.my.walletapp.service.account;

import lombok.RequiredArgsConstructor;
import org.my.walletapp.dto.account.AccountRequest;
import org.my.walletapp.dto.account.AccountResponse;
import org.my.walletapp.entity.Account;
import org.my.walletapp.entity.User;
import org.my.walletapp.exception.ResourceNotFoundException;
import org.my.walletapp.mapper.AccountMapper;
import org.my.walletapp.repository.AccountRepository;
import org.my.walletapp.repository.UserRepository;
import org.my.walletapp.service.transaction.ExchangeRateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final UserRepository userRepository;

    private final ExchangeRateService exchangeRateService;

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts(Long userId) {
        return accountRepository.findAllByUserIdAndIsActiveTrue(userId)
                .stream()
                .map(accountMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AccountResponse createAccount(Long userId, AccountRequest request) {
        Account account = accountMapper.toEntity(request);

        User userProxy = userRepository.getReferenceById(userId);
        account.setUser(userProxy);

        Account savedAccount = accountRepository.save(account);
        return accountMapper.toResponse(savedAccount);
    }

    @Override
    @Transactional
    public AccountResponse updateAccountById(Long userId, Long accountId, AccountRequest request) {
        Account account = accountRepository.findByIdAndUserIdAndIsActiveTrue(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account with id " + accountId + " not found"));

        String oldCurrency = account.getCurrency();
        String newCurrency = request.currency();

        if (newCurrency != null && !newCurrency.equalsIgnoreCase(oldCurrency)) {
            if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal exchangeRate = exchangeRateService.getRate(oldCurrency, newCurrency);
                BigDecimal newBalance = account.getBalance().multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
                account.setBalance(newBalance);
            }
            account.setCurrency(newCurrency);
        }

        accountMapper.partialUpdate(request, account);

        account.setDescription(request.description());

        return accountMapper.toResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long userId, Long accountId) {
        Account account = accountRepository.findByIdAndUserIdAndIsActiveTrue(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account with id " + accountId + " not found"));

        return accountMapper.toResponse(account);
    }

    @Override
    @Transactional
    public void deleteAccountById(Long userId, Long accountId) {
        Account account = accountRepository.findByIdAndUserIdAndIsActiveTrue(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account with id " + accountId + " not found"));

        account.setActive(false);
    }
}
