package org.my.walletapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.my.walletapp.dto.merchant.MerchantRequest;
import org.my.walletapp.dto.merchant.MerchantResponse;
import org.my.walletapp.entity.Merchant;
import org.my.walletapp.entity.User;
import org.my.walletapp.exception.ResourceNotFoundException;
import org.my.walletapp.mapper.MerchantMapper;
import org.my.walletapp.repository.MerchantRepository;
import org.my.walletapp.repository.UserRepository;
import org.my.walletapp.service.merchant.MerchantServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MerchantServiceTest {

    @Mock
    private MerchantRepository merchantRepository;
    @Mock
    private MerchantMapper merchantMapper;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MerchantServiceImpl merchantService;

    private User testUser;
    private Merchant testMerchant;
    private final Long userId = 1L;
    private final Long merchantId = 10L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Artem");
        testUser.setEmail("melnyk.a.yu.-io46@edu.kpi.ua");
        testUser.setPassword("Art1634*");

        testMerchant = new Merchant();
        testMerchant.setId(merchantId);
        testMerchant.setName("Silpo");
        testMerchant.setActive(true);
        testMerchant.setUser(testUser);
    }

    @Nested
    class GetAllMerchantsTests {

        @Test
        void getAllMerchants_Success() {
            MerchantResponse mockResponse = new MerchantResponse(merchantId, null, "Silpo", null, true);

            when(merchantRepository.findAllByUserIdAndIsActiveTrue(userId)).thenReturn(List.of(testMerchant));
            when(merchantMapper.toResponse(testMerchant)).thenReturn(mockResponse);

            List<MerchantResponse> result = merchantService.getAllMerchants(userId);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Silpo", result.getFirst().name());
            verify(merchantRepository, times(1)).findAllByUserIdAndIsActiveTrue(userId);
        }
    }

    @Nested
    class CreateMerchantTests {

        @Test
        void createMerchant_Success() {
            MerchantRequest request = new MerchantRequest(null, "Novus", null);
            Merchant mappedMerchant = new Merchant();
            mappedMerchant.setName("Novus");
            MerchantResponse mockResponse = new MerchantResponse(11L, null, "Novus", null, true);

            when(merchantMapper.toEntity(request)).thenReturn(mappedMerchant);
            when(userRepository.getReferenceById(userId)).thenReturn(testUser);
            when(merchantRepository.save(mappedMerchant)).thenReturn(mappedMerchant);
            when(merchantMapper.toResponse(mappedMerchant)).thenReturn(mockResponse);

            MerchantResponse result = merchantService.createMerchant(userId, request);

            assertNotNull(result);
            assertEquals("Novus", result.name());
            assertEquals(testUser, mappedMerchant.getUser());
            verify(merchantRepository, times(1)).save(mappedMerchant);
        }
    }

    @Nested
    class UpdateMerchantTests {

        @Test
        void updateMerchantById_Success() {
            MerchantRequest request = new MerchantRequest(null, "Updated Silpo", null);
            MerchantResponse mockResponse = new MerchantResponse(merchantId, null, "Updated Silpo", null, true);

            when(merchantRepository.findByIdAndUserIdAndIsActiveTrue(merchantId, userId)).thenReturn(Optional.of(testMerchant));
            when(merchantRepository.save(any(Merchant.class))).thenReturn(testMerchant);
            when(merchantMapper.toResponse(testMerchant)).thenReturn(mockResponse);

            MerchantResponse result = merchantService.updateMerchantById(userId, merchantId, request);

            assertNotNull(result);
            assertEquals("Updated Silpo", result.name());
            verify(merchantMapper, times(1)).partialUpdate(request, testMerchant);
        }

        @Test
        void updateMerchantById_ThrowsResourceNotFoundException() {
            MerchantRequest request = new MerchantRequest(null, "Updated Silpo", null);

            when(merchantRepository.findByIdAndUserIdAndIsActiveTrue(merchantId, userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> merchantService.updateMerchantById(userId, merchantId, request));
            verify(merchantMapper, never()).partialUpdate(any(), any());
        }
    }

    @Nested
    class GetMerchantTests {

        @Test
        void getMerchantById_Success() {
            MerchantResponse mockResponse = new MerchantResponse(merchantId, null, "Silpo", null, true);

            when(merchantRepository.findByIdAndUserIdAndIsActiveTrue(merchantId, userId)).thenReturn(Optional.of(testMerchant));
            when(merchantMapper.toResponse(testMerchant)).thenReturn(mockResponse);

            MerchantResponse result = merchantService.getMerchantById(userId, merchantId);

            assertNotNull(result);
            assertEquals("Silpo", result.name());
        }

        @Test
        void getMerchantById_ThrowsResourceNotFoundException() {
            when(merchantRepository.findByIdAndUserIdAndIsActiveTrue(merchantId, userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> merchantService.getMerchantById(userId, merchantId));
        }
    }

    @Nested
    class DeleteMerchantTests {

        @Test
        void deleteMerchantById_Success() {
            when(merchantRepository.findByIdAndUserIdAndIsActiveTrue(merchantId, userId)).thenReturn(Optional.of(testMerchant));

            merchantService.deleteMerchantById(userId, merchantId);

            assertFalse(testMerchant.isActive());
        }

        @Test
        void deleteMerchantById_ThrowsResourceNotFoundException() {
            when(merchantRepository.findByIdAndUserIdAndIsActiveTrue(merchantId, userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> merchantService.deleteMerchantById(userId, merchantId));
        }
    }
}