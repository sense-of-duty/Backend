package org.example.gdgpage.dto.auth.response;


import lombok.Builder;
import lombok.Getter;
import org.example.gdgpage.dto.token.TokenDto;

@Getter
@Builder
public class LoginResponse {

    private TokenDto token;
    private UserResponse user;

    public static LoginResponse of(TokenDto token, UserResponse user) {
        return LoginResponse.builder()
                .token(token)
                .user(user)
                .build();
    }
}
