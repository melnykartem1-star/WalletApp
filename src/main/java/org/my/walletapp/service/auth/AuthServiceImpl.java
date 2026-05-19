package org.my.walletapp.service.auth;

import lombok.RequiredArgsConstructor;
import org.my.walletapp.dto.auth.AuthResponse;
import org.my.walletapp.dto.auth.LoginRequest;
import org.my.walletapp.dto.auth.RegisterRequest;
import org.my.walletapp.dto.token.RefreshTokenRequest;
import org.my.walletapp.dto.token.RefreshTokenResponse;
import org.my.walletapp.entity.User;
import org.my.walletapp.exception.EmailAlreadyExistsException;
import org.my.walletapp.exception.InvalidRefreshToken;
import org.my.walletapp.exception.ResourceNotFoundException;
import org.my.walletapp.repository.user.UserRepository;
import org.my.walletapp.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setLastLogin(LocalDateTime.now().withNano(0));

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                jwtService.generateToken(user),
                jwtService.generateRefreshToken(user)
        );
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email is already taken");
        }

        User user = new User(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password()),
                "en-GB",
                ZoneId.of("UTC")
        );
        User savedUser = userRepository.save(user);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                jwtService.generateToken(savedUser),
                jwtService.generateRefreshToken(savedUser)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();
        String userEmail = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (jwtService.isTokenValid(refreshToken, user)) {
            return new RefreshTokenResponse(
                    jwtService.generateToken(user),
                    jwtService.generateRefreshToken(user)
            );
        }

        throw new InvalidRefreshToken("Invalid refresh token");
    }
}
