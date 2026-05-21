package org.my.walletapp.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.my.walletapp.config.JwtProperties;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;

    private final String SECRET_KEY = "c29tZS1zZWNyZXQta2V5LXRoYXQtaXMtc3VmZmljaWVudGx5LWxvbmctZm9yLWhzMjU2";

    @BeforeEach
    void setUp() {
        when(jwtProperties.getSecretKey()).thenReturn(SECRET_KEY);
        when(jwtProperties.getExpiration()).thenReturn(3600000L); // 1 година

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void extractUsername_ShouldReturnCorrectSubject() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);

        assertEquals("test@example.com", username);
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenIsValid() {
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenUsernameDiffers() {
        String token = jwtService.generateToken(userDetails);

        UserDetails anotherUser = mock(UserDetails.class);
        when(anotherUser.getUsername()).thenReturn("other@example.com");

        boolean isValid = jwtService.isTokenValid(token, anotherUser);

        assertFalse(isValid);
    }
}