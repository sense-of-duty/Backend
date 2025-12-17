package org.example.gdgpage.service.finder;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.common.Constants;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.domain.refresh.RefreshToken;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.exception.NotFoundException;
import org.example.gdgpage.exception.UnauthorizedException;
import org.example.gdgpage.jwt.TokenProvider;
import org.example.gdgpage.repository.RefreshTokenRepository;
import org.example.gdgpage.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class FindUserImpl implements FindUser {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Override
    public Long getUserIdFromRefreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new UnauthorizedException(ErrorMessage.NEED_TO_LOGIN);
        }

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException(ErrorMessage.INVALID_TOKEN);
        }

        Claims claims = tokenProvider.parseClaim(refreshToken);
        String tokenType = claims.get(Constants.TOKEN_TYPE, String.class);

        if (!Constants.REFRESH_TOKEN.equals(tokenType)) {
            throw new UnauthorizedException(ErrorMessage.INVALID_TOKEN);
        }

        Long userId = Long.parseLong(claims.getSubject());

        RefreshToken stored = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException(ErrorMessage.INVALID_TOKEN));

        if (!stored.getUserId().equals(userId)) {
            throw new UnauthorizedException(ErrorMessage.INVALID_TOKEN);
        }

        return userId;
    }

    public Long getUserIdFromAccessToken(String accessToken) {

        if (!StringUtils.hasText(accessToken)) {
            throw new UnauthorizedException(ErrorMessage.NEED_TO_LOGIN);
        }

        String token = accessToken.replace("Bearer ", "");

        if (!tokenProvider.validateToken(token)) {
            throw new UnauthorizedException(ErrorMessage.INVALID_TOKEN);
        }

        Claims claims = tokenProvider.parseClaim(token);

        String tokenType = claims.get(Constants.TOKEN_TYPE, String.class);
        if (!Constants.ACCESS_TOKEN.equals(tokenType)) {
            throw new UnauthorizedException(ErrorMessage.INVALID_TOKEN);
        }

        return Long.parseLong(claims.getSubject());
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));
    }
}
