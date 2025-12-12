package org.example.gdgpage.dto.admin.request;

import org.example.gdgpage.domain.auth.PartType;
import org.example.gdgpage.domain.auth.Role;

public record UpdateUserRequest(
        Role role,
        PartType part
) {}
