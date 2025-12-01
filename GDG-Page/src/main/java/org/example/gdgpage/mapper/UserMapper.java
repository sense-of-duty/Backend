package org.example.gdgpage.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.dto.user.response.UserResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    public static UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .part(user.getPart())
                .role(user.getRole())
                .isApproved(user.isApproved())
                .isProfileCompleted(user.isProfileCompleted())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
