package org.my.walletapp.service.merchant;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.my.walletapp.dto.merchant.MerchantRequest;
import org.my.walletapp.dto.merchant.MerchantResponse;
import org.my.walletapp.entity.Category;
import org.my.walletapp.entity.Merchant;
import org.my.walletapp.exception.ResourceNotFoundException;
import org.my.walletapp.mapper.MerchantMapper;
import org.my.walletapp.repository.CategoryRepository;
import org.my.walletapp.repository.MerchantRepository;
import org.my.walletapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;
    private final MerchantMapper merchantMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MerchantResponse> getAllMerchants(Long userId) {
        return merchantRepository.findAllByUserIdAndIsActiveTrue(userId)
                .stream()
                .map(merchantMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public MerchantResponse createMerchant(Long userId, MerchantRequest request) {
        Merchant merchant = merchantMapper.toEntity(request);

        merchant.setUser(userRepository.getReferenceById(userId));

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Категорію не знайдено"));
            merchant.setCategory(category);
        }

        Merchant savedMerchant = merchantRepository.save(merchant);
        return merchantMapper.toResponse(savedMerchant);
    }

    @Override
    @Transactional
    public MerchantResponse updateMerchantById(Long userId, Long merchantId, MerchantRequest request) {
        Merchant merchant = merchantRepository.findByIdAndUserIdAndIsActiveTrue(merchantId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Продавця не знайдено"));

        merchantMapper.partialUpdate(request, merchant);

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Категорію не знайдено"));
            merchant.setCategory(category);
        } else {
            merchant.setCategory(null);
        }

        Merchant updatedMerchant = merchantRepository.save(merchant);
        return merchantMapper.toResponse(updatedMerchant);
    }

    @Override
    @Transactional(readOnly = true)
    public MerchantResponse getMerchantById(Long userId, Long merchantId) {
        Merchant merchant = merchantRepository.findByIdAndUserIdAndIsActiveTrue(merchantId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant with id " + merchantId + " not found"));

        return merchantMapper.toResponse(merchant);
    }

    @Override
    @Transactional
    public void deleteMerchantById(Long userId, Long merchantId) {
        Merchant merchant = merchantRepository.findByIdAndUserIdAndIsActiveTrue(merchantId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant with id " + merchantId + " not found"));

        merchant.setActive(false);
    }
}