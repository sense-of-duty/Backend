package org.example.gdgpage.service.admin;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.dto.admin.request.UpdateUserRequest;
import org.example.gdgpage.dto.admin.request.UserApproveRequest;
import org.example.gdgpage.dto.admin.request.UserRejectRequest;
import org.example.gdgpage.dto.user.response.UserResponse;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.NotFoundException;
import org.example.gdgpage.mapper.UserMapper;
import org.example.gdgpage.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getSignupRequests() {
        return userRepository.findByIsApprovedFalseAndRejectionReasonIsNull()
                .stream()
                .map(UserMapper::toUserResponse)
                .toList();
    }

    @Transactional
    public void updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));

        if (updateUserRequest.role() != null) {
            user.updateRole(updateUserRequest.role());
        }

        if (updateUserRequest.part() != null) {
            user.updatePart(updateUserRequest.part());
        }
    }

    @Transactional
    public void kickUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));

        user.deactivate();
    }

    @Transactional
    public void approveUsers(UserApproveRequest userApproveRequest) {
        Map<Long, User> users = loadUsers(userApproveRequest.userIds());

        users.values()
                .forEach(User::approve);
    }

    @Transactional
    public void rejectUsers(UserRejectRequest userRejectRequest) {
        if (userRejectRequest.reason() == null || userRejectRequest.reason().isBlank()) {
            throw new BadRequestException(ErrorMessage.INVALID_REQUEST);
        }

        Map<Long, User> users = loadUsers(userRejectRequest.userIds());

        users.values()
                .forEach(user -> user.reject(userRejectRequest.reason()));
    }

    private Map<Long, User> loadUsers(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);

        if (users.size() != userIds.size()) {
            throw new BadRequestException(ErrorMessage.NOT_EXIST_USER);
        }

        return users
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
