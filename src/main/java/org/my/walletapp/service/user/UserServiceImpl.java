package org.my.walletapp.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.my.walletapp.dto.user.PasswordRequest;
import org.my.walletapp.dto.user.UserProfileRequest;
import org.my.walletapp.dto.user.UserProfileResponse;
import org.my.walletapp.entity.User;
import org.my.walletapp.exception.EmailAlreadyExistsException;
import org.my.walletapp.exception.IdenticalPasswordsException;
import org.my.walletapp.exception.ResourceNotFoundException;
import org.my.walletapp.exception.WrongPasswordException;
import org.my.walletapp.mapper.UserMapper;
import org.my.walletapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserProfileResponse updateUserProfile(Long userId, UserProfileRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

        if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email is already taken");
        }

        userMapper.partialUpdate(request, user);

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void changeUserPassword(Long userId, PasswordRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new WrongPasswordException("The old password you entered is incorrect");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new IdenticalPasswordsException("New password cannot be the same as the old one");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        userRepository.delete(user);
    }
}
