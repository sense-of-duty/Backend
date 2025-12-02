package org.example.gdgpage.service.finder;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.common.Constants;
import org.example.gdgpage.domain.refresh.RefreshToken;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.jwt.TokenProvider;
import org.example.gdgpage.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class FindUserImpl implements FindUser {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public Long getUserIdFromRefreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new BadRequestException(ErrorMessage.NEED_TO_LOGIN);
        }

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException(ErrorMessage.INVALID_TOKEN);
        }

        Claims claims = tokenProvider.parseClaim(refreshToken);
        String tokenType = claims.get(Constants.TOKEN_TYPE, String.class);

        if (!Constants.REFRESH_TOKEN.equals(tokenType)) {
            throw new BadRequestException(ErrorMessage.INVALID_TOKEN);
        }

        Long userId = Long.parseLong(claims.getSubject());

        RefreshToken stored = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_TOKEN));

        if (!stored.getUserId().equals(userId)) {
            throw new BadRequestException(ErrorMessage.INVALID_TOKEN);
        }

        return userId;
    }
}

