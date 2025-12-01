package org.example.gdgpage.dto.auth.response;

import lombok.Builder;
import org.example.gdgpage.dto.token.TokenDto;

@Builder
public record LoginResponse (

    TokenDto token,
    UserResponse user
) {}
