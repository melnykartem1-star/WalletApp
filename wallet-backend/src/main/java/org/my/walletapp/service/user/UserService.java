package org.my.walletapp.service.user;

import org.my.walletapp.dto.user.PasswordRequest;
import org.my.walletapp.dto.user.UserProfilePatchRequest;
import org.my.walletapp.dto.user.UserProfileResponse;

public interface UserService {

    UserProfileResponse updateUserProfile(Long userId, UserProfilePatchRequest request);
    void changeUserPassword(Long userId, PasswordRequest request);
    UserProfileResponse getUserById(Long userId);
    void deleteUserById(Long userId);

}
