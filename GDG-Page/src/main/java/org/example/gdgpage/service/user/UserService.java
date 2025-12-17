package org.example.gdgpage.service.user;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.dto.user.request.UpdatePasswordRequest;
import org.example.gdgpage.dto.user.response.UserResponse;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.NotFoundException;
import org.example.gdgpage.mapper.auth.UserMapper;
import org.example.gdgpage.repository.auth.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileImageStorage profileImageStorage;

    @Transactional(readOnly = true)
    public UserResponse getMyProfile(Long userId) {
        User user = getUser(userId);
        return UserMapper.toUserResponse(user);
    }

    @Transactional
    public void changePassword(Long userId, UpdatePasswordRequest request) {
        User user = getUser(userId);

        if (user.getPassword() == null) {
            throw new BadRequestException(ErrorMessage.OAUTH_ACCOUNT_CANNOT_CHANGE_PASSWORD);
        }

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadRequestException(ErrorMessage.WRONG_CURRENT_PASSWORD);
        }

        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new BadRequestException(ErrorMessage.WRONG_CHECK_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(request.newPassword()));
    }

    @Transactional
    public UserResponse updateProfileImage(Long userId, MultipartFile file) {
        User user = getUser(userId);

        String imageUrl = profileImageStorage.storeProfileImage(userId, file);
        user.updateProfileImage(imageUrl);

        return UserMapper.toUserResponse(user);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));
    }
}
