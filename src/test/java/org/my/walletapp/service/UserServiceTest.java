package org.my.walletapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.my.walletapp.dto.user.PasswordRequest;
import org.my.walletapp.dto.user.UserProfilePatchRequest;
import org.my.walletapp.dto.user.UserProfileResponse;
import org.my.walletapp.entity.User;
import org.my.walletapp.exception.EmailAlreadyExistsException;
import org.my.walletapp.exception.IdenticalPasswordsException;
import org.my.walletapp.exception.ResourceNotFoundException;
import org.my.walletapp.exception.WrongPasswordException;
import org.my.walletapp.mapper.UserMapper;
import org.my.walletapp.repository.UserRepository;
import org.my.walletapp.service.user.UserServiceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Artem");
        testUser.setEmail("melnyk.a.yu.-io46@edu.kpi.ua");
        testUser.setPassword("Art1634*");
    }

    @Nested
    class UpdateUserProfileTests {

        @Test
        void updateUserProfile_Success_WithNewEmail() {
            UserProfilePatchRequest request = new UserProfilePatchRequest("New Name", "newemail@example.com", "uk-UA", "UTC");
            UserProfileResponse mockResponse = new UserProfileResponse(userId, "New Name", "newemail@example.com", "uk-UA", "UTC", null, null);

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(userMapper.toResponse(testUser)).thenReturn(mockResponse);

            UserProfileResponse result = userService.updateUserProfile(userId, request);

            assertNotNull(result);
            assertEquals("newemail@example.com", result.email());
            assertEquals("New Name", result.name());

            verify(userMapper, times(1)).partialUpdate(request, testUser);
        }

        @Test
        void updateUserProfile_ThrowsEmailAlreadyExistsException_WhenEmailIsTaken() {
            UserProfilePatchRequest request = new UserProfilePatchRequest("New Name", "taken@example.com", null, null);

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail(request.email())).thenReturn(true);

            assertThrows(EmailAlreadyExistsException.class, () -> userService.updateUserProfile(userId, request));
            verify(userMapper, never()).partialUpdate(any(), any());
        }

        @Test
        void updateUserProfile_ThrowsResourceNotFoundException_WhenUserDoesNotExist() {
            UserProfilePatchRequest request = new UserProfilePatchRequest("Name", null, null, null);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> userService.updateUserProfile(userId, request));
        }

        @Test
        void updateUserProfile_Success_PartialUpdateOnlyName() {
            UserProfilePatchRequest request = new UserProfilePatchRequest("Updated Name", null, null, null);
            UserProfileResponse mockResponse = new UserProfileResponse(userId, "Updated Name", "melnyk.a.yu.-io46@edu.kpi.ua", "en-GB", "UTC", null, null);

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(userMapper.toResponse(testUser)).thenReturn(mockResponse);

            UserProfileResponse result = userService.updateUserProfile(userId, request);

            assertNotNull(result);
            assertEquals("Updated Name", result.name());
            assertEquals("melnyk.a.yu.-io46@edu.kpi.ua", result.email());

            verify(userRepository, never()).existsByEmail(anyString());
            verify(userMapper, times(1)).partialUpdate(request, testUser);
        }
    }

    @Nested
    class ChangeUserPasswordTests {

        @Test
        void changeUserPassword_Success() {
            PasswordRequest request = new PasswordRequest("Art1634*", "NewPassword123!");

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(request.oldPassword(), testUser.getPassword())).thenReturn(true);
            when(passwordEncoder.matches(request.newPassword(), testUser.getPassword())).thenReturn(false);
            when(passwordEncoder.encode(request.newPassword())).thenReturn("encodedNewPassword");

            userService.changeUserPassword(userId, request);

            assertEquals("encodedNewPassword", testUser.getPassword());
        }

        @Test
        void changeUserPassword_ThrowsWrongPasswordException_WhenOldPasswordIsIncorrect() {
            PasswordRequest request = new PasswordRequest("WrongOldPassword", "NewPassword123!");

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(request.oldPassword(), testUser.getPassword())).thenReturn(false);

            assertThrows(WrongPasswordException.class, () -> userService.changeUserPassword(userId, request));
            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        void changeUserPassword_ThrowsIdenticalPasswordsException_WhenNewPasswordIsSameAsOld() {
            PasswordRequest request = new PasswordRequest("Art1634*", "OldPassword123!");

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(request.oldPassword(), testUser.getPassword())).thenReturn(true);
            when(passwordEncoder.matches(request.newPassword(), testUser.getPassword())).thenReturn(true);

            assertThrows(IdenticalPasswordsException.class, () -> userService.changeUserPassword(userId, request));
            verify(passwordEncoder, never()).encode(anyString());
        }
    }

    @Nested
    class GetAndDeleteUserTests {

        @Test
        void getUserById_Success() {
            UserProfileResponse mockResponse = new UserProfileResponse(userId, "Artem", "melnyk.a.yu.-io46@edu.kpi.ua", "en-GB", "UTC", null, null);

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(userMapper.toResponse(testUser)).thenReturn(mockResponse);

            UserProfileResponse result = userService.getUserById(userId);

            assertNotNull(result);
            assertEquals("Artem", result.name());
        }

        @Test
        void deleteUserById_Success() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

            userService.deleteUserById(userId);

            verify(userRepository, times(1)).delete(testUser);
        }

        @Test
        void deleteUserById_ThrowsResourceNotFoundException() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> userService.deleteUserById(userId));
            verify(userRepository, never()).delete(any(User.class));
        }
    }
}