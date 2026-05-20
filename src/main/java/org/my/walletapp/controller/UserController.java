package org.my.walletapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.my.walletapp.dto.user.PasswordRequest;
import org.my.walletapp.dto.user.UserProfilePatchRequest;
import org.my.walletapp.dto.user.UserProfileResponse;
import org.my.walletapp.entity.User;
import org.my.walletapp.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfileByJwt(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getUserById(user.getId()));
    }

    @PatchMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateUserProfileByJwt(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserProfilePatchRequest request) {
        return ResponseEntity.ok(userService.updateUserProfile(user.getId(), request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUserProfileByJwt(@AuthenticationPrincipal User user) {
        userService.deleteUserById(user.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changeUserPasswordByJwt(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PasswordRequest request) {
        userService.changeUserPassword(user.getId(), request);
        return ResponseEntity.noContent().build();
    }
}