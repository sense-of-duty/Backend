package org.example.gdgpage.dto.auth.response;

import lombok.Builder;
import lombok.Getter;
import org.example.gdgpage.domain.auth.PartType;
import org.example.gdgpage.domain.auth.Role;

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
}
