package org.example.gdgpage.dto.auth.response;

import lombok.Builder;
import lombok.Getter;
import org.example.gdgpage.domain.auth.PartType;
import org.example.gdgpage.domain.auth.Role;
import org.example.gdgpage.domain.auth.User;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String name;
    private String phone;
    private PartType part;
    private Role role;
    private boolean isApproved;
    private boolean isProfileCompleted;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    public static UserResponse from(User user) {
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
