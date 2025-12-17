package org.example.gdgpage.dto.user.response;

import lombok.Builder;
import org.example.gdgpage.domain.auth.PartType;
import org.example.gdgpage.domain.auth.Role;

import java.time.LocalDateTime;

@Builder
public record UserResponse (

    Long id,
    String email,
    String name,
    String phone,
    PartType part,
    Role role,
    boolean isApproved,
    boolean isProfileCompleted,
    boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime lastLoginAt,
    String profileImageUrl,
    boolean emailVerified,
    String rejectionReason
) {}
