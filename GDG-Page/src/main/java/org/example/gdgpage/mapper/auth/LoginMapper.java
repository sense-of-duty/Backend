package org.example.gdgpage.mapper.auth;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.gdgpage.dto.auth.response.LoginResponse;
import org.example.gdgpage.dto.user.response.UserResponse;
import org.example.gdgpage.dto.token.TokenDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginMapper {

    public static LoginResponse of(TokenDto token, UserResponse user) {
        return LoginResponse.builder()
                .token(token)
                .user(user)
                .build();
    }
}
