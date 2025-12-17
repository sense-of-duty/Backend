package org.example.gdgpage.mapper.auth;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.gdgpage.common.Constants;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.dto.user.response.UserResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    public static UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        String profileImageUrl = user.getProfileImageUrl();

        if (profileImageUrl == null || profileImageUrl.isBlank()) {
            profileImageUrl = Constants.DEFAULT_PROFILE_IMAGE_URL;
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
                .rejectionReason(user.getRejectionReason())
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
