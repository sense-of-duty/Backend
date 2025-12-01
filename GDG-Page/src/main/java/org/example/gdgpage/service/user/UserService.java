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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));

        return UserMapper.toUserResponse(user);
    }

    @Transactional
    public void changePassword(Long userId, UpdatePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadRequestException(ErrorMessage.WRONG_CURRENT_PASSWORD);
        }

        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new BadRequestException(ErrorMessage.WRONG_CHECK_PASSWORD);
        }

        String encoded = passwordEncoder.encode(request.newPassword());
        user.updatePassword(encoded);
    }
}
