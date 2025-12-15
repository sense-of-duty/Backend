package org.example.gdgpage.service.user;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.dto.user.request.UpdatePasswordRequest;
import org.example.gdgpage.dto.user.response.UserResponse;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.NotFoundException;
import org.example.gdgpage.mapper.UserMapper;
import org.example.gdgpage.repository.UserRepository;
import org.example.gdgpage.service.finder.FindUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FindUser findUser;
    private final ProfileImageStorage profileImageStorage;

    @Transactional(readOnly = true)
    public UserResponse getMyProfile(String refreshToken) {
        User user = getUserFromRefreshToken(refreshToken);

        return UserMapper.toUserResponse(user);
    }

    @Transactional
    public void changePassword(String refreshToken, UpdatePasswordRequest updatePasswordRequest) {
        User user = getUserFromRefreshToken(refreshToken);

        if (user.getPassword() == null) {
            throw new BadRequestException(ErrorMessage.OAUTH_ACCOUNT_CANNOT_CHANGE_PASSWORD);
        }

        if (!passwordEncoder.matches(updatePasswordRequest.currentPassword(), user.getPassword())) {
            throw new BadRequestException(ErrorMessage.WRONG_CURRENT_PASSWORD);
        }

        if (!updatePasswordRequest.newPassword().equals(updatePasswordRequest.confirmNewPassword())) {
            throw new BadRequestException(ErrorMessage.WRONG_CHECK_PASSWORD);
        }

        String encoded = passwordEncoder.encode(updatePasswordRequest.newPassword());
        user.updatePassword(encoded);
    }

    private User getUserFromRefreshToken(String refreshToken) {
        Long userId = findUser.getUserIdFromRefreshToken(refreshToken);

        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));
    }

    @Transactional
    public UserResponse updateProfileImage(String refreshToken, MultipartFile file) {
        Long userId = findUser.getUserIdFromRefreshToken(refreshToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));

        String imageUrl = profileImageStorage.storeProfileImage(userId, file);
        user.updateProfileImage(imageUrl);

        return UserMapper.toUserResponse(user);
    }
}
