package org.my.walletapp.service.user;

import org.my.walletapp.dto.password.PasswordRequest;
import org.my.walletapp.dto.user.UserProfileRequest;
import org.my.walletapp.dto.user.UserProfileResponse;

public interface UserService {

    UserProfileResponse updateUserProfile(Long userId, UserProfileRequest request);
    void changeUserPassword(Long userId, PasswordRequest request);
    UserProfileResponse getUserById(Long userId);
    void deleteUserById(Long userId);

}
