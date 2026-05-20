package org.my.walletapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.my.walletapp.dto.account.AccountRequest;
import org.my.walletapp.dto.account.AccountResponse;
import org.my.walletapp.entity.User;
import org.my.walletapp.service.account.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getAllAccounts(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(
            @AuthenticationPrincipal User user,
            @PathVariable(name = "id") Long accountId) {
        return ResponseEntity.ok(accountService.getAccountById(user.getId(), accountId));
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.createAccount(user.getId(), request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAccountById(
            @AuthenticationPrincipal User user,
            @PathVariable(name = "id") Long accountId,
            @Valid @RequestBody AccountRequest request) {
        return ResponseEntity.ok(accountService.updateAccountById(user.getId(), accountId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccountById(
            @AuthenticationPrincipal User user,
            @PathVariable(name = "id") Long accountId) {
        accountService.deleteAccountById(user.getId(), accountId);
        return ResponseEntity.noContent().build();
    }
}