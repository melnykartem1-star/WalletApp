package org.my.walletapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.my.walletapp.dto.merchant.MerchantRequest;
import org.my.walletapp.dto.merchant.MerchantResponse;
import org.my.walletapp.entity.User;
import org.my.walletapp.service.merchant.MerchantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @GetMapping
    public ResponseEntity<List<MerchantResponse>> getAllMerchants(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(merchantService.getAllMerchants(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MerchantResponse> getMerchantById(
            @AuthenticationPrincipal User user,
            @PathVariable(name = "id") Long merchantId) {
        return ResponseEntity.ok(merchantService.getMerchantById(user.getId(), merchantId));
    }

    @PostMapping
    public ResponseEntity<MerchantResponse> createMerchant(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody MerchantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(merchantService.createMerchant(user.getId(), request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MerchantResponse> updateMerchantById(
            @AuthenticationPrincipal User user,
            @PathVariable(name = "id") Long merchantId,
            @Valid @RequestBody MerchantRequest request) {
        return ResponseEntity.ok(merchantService.updateMerchantById(user.getId(), merchantId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMerchantById(
            @AuthenticationPrincipal User user,
            @PathVariable(name = "id") Long merchantId) {
        merchantService.deleteMerchantById(user.getId(), merchantId);
        return ResponseEntity.noContent().build();
    }

}
