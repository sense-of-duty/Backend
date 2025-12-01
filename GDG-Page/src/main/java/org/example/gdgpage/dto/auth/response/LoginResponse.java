package org.example.gdgpage.dto.auth.response;

import lombok.Builder;
import org.example.gdgpage.dto.token.TokenDto;
import org.example.gdgpage.dto.user.response.UserResponse;

@Builder
public record LoginResponse (

    TokenDto token,
    UserResponse user
) {}
