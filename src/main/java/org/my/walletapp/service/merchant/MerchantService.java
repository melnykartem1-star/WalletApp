package org.my.walletapp.service.merchant;

import org.my.walletapp.dto.merchant.MerchantRequest;
import org.my.walletapp.dto.merchant.MerchantResponse;

import java.util.List;

public interface MerchantService {
    List<MerchantResponse> getAllMerchants(Long userId);
    MerchantResponse createMerchant(Long userId, MerchantRequest request);
    MerchantResponse updateMerchantById(Long userId, Long merchantId, MerchantRequest request);
    MerchantResponse getMerchantById(Long userId, Long merchantId);
    void deleteMerchantById(Long userId, Long merchantId);

}
