package org.my.walletapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.my.walletapp.dto.auth.AuthResponse;
import org.my.walletapp.dto.auth.LoginRequest;
import org.my.walletapp.dto.auth.RegisterRequest;
import org.my.walletapp.dto.token.RefreshTokenRequest;
import org.my.walletapp.dto.token.RefreshTokenResponse;
import org.my.walletapp.entity.User;
import org.my.walletapp.exception.EmailAlreadyExistsException;
import org.my.walletapp.exception.InvalidRefreshToken;
import org.my.walletapp.exception.ResourceNotFoundException;
import org.my.walletapp.repository.UserRepository;
import org.my.walletapp.security.JwtService;
import org.my.walletapp.service.auth.AuthServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Artem");
        testUser.setEmail("melnyk.a.yu.-io46@edu.kpi.ua");
        testUser.setPassword("Art1634*");
    }

    @Nested
    class LoginTests {

        @Test
        void login_Success() {
            LoginRequest request = new LoginRequest("melnyk.a.yu.-io46@edu.kpi.ua", "Art1634*");
            Authentication authentication = mock(Authentication.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(jwtService.generateToken(testUser)).thenReturn("mockAccessToken");
            when(jwtService.generateRefreshToken(testUser)).thenReturn("mockRefreshToken");
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            AuthResponse response = authService.login(request);

            assertNotNull(response);
            assertEquals(testUser.getId(), response.id());
            assertEquals(testUser.getEmail(), response.email());
            assertEquals("mockAccessToken", response.accessToken());
            assertEquals("mockRefreshToken", response.refreshToken());

            verify(userRepository, times(1)).save(testUser);
        }
    }

    @Nested
    class RegisterTests {

        @Test
        void register_Success() {
            // Arrange
            RegisterRequest request = new RegisterRequest("Artem", "melnyk.a.yu.-io46@edu.kpi.ua", "Art1634*");

            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(jwtService.generateToken(any(User.class))).thenReturn("mockAccessToken");
            when(jwtService.generateRefreshToken(any(User.class))).thenReturn("mockRefreshToken");

            AuthResponse response = authService.register(request);

            assertNotNull(response);
            assertEquals("mockAccessToken", response.accessToken());
            assertEquals("mockRefreshToken", response.refreshToken());

            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void register_ThrowsEmailAlreadyExistsException() {
            RegisterRequest request = new RegisterRequest("Artem", "melnyk.a.yu.-io46@edu.kpi.ua", "Art1634*");
            when(userRepository.existsByEmail(request.email())).thenReturn(true);

            assertThrows(EmailAlreadyExistsException.class, () -> authService.register(request));

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    class RefreshTokenTests {

        @Test
        void refresh_Success() {
            RefreshTokenRequest request = new RefreshTokenRequest("validRefreshToken");

            when(jwtService.extractUsername("validRefreshToken")).thenReturn("melnyk.a.yu.-io46@edu.kpi.ua");
            when(userRepository.findByEmail("melnyk.a.yu.-io46@edu.kpi.ua")).thenReturn(Optional.of(testUser));
            when(jwtService.isTokenValid("validRefreshToken", testUser)).thenReturn(true);
            when(jwtService.generateToken(testUser)).thenReturn("newAccessToken");
            when(jwtService.generateRefreshToken(testUser)).thenReturn("newRefreshToken");

            RefreshTokenResponse response = authService.refresh(request);

            assertNotNull(response);
            assertEquals("newAccessToken", response.accessToken());
            assertEquals("newRefreshToken", response.refreshToken());
        }

        @Test
        void refresh_ThrowsResourceNotFoundException_WhenUserNotFound() {
            RefreshTokenRequest request = new RefreshTokenRequest("someToken");
            when(jwtService.extractUsername("someToken")).thenReturn("unknown@example.com");
            when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> authService.refresh(request));
        }

        @Test
        void refresh_ThrowsInvalidRefreshToken_WhenTokenInvalid() {
            RefreshTokenRequest request = new RefreshTokenRequest("invalidToken");

            when(jwtService.extractUsername("invalidToken")).thenReturn("melnyk.a.yu.-io46@edu.kpi.ua");
            when(userRepository.findByEmail("melnyk.a.yu.-io46@edu.kpi.ua")).thenReturn(Optional.of(testUser));
            when(jwtService.isTokenValid("invalidToken", testUser)).thenReturn(false);

            assertThrows(InvalidRefreshToken.class, () -> authService.refresh(request));
        }
    }
}
