package org.example.gdgpage.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.bcel.Const;
import org.example.gdgpage.common.Constants;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
public class TokenProvider {

    private static final String ROLE_CLAIM = "role";
    private static final String DELIMITER = ",";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String ROLE_PREFIX = "ROLE_";

    private final SecretKey key;
    private final long accessTokenValidityTime;
    private final long refreshTokenValidityTime;

    public TokenProvider(@Value("${jwt.secret}") String secretKey,
                         @Value("${jwt.access-token-validity-in-milliseconds}") long accessTokenValidity,
                         @Value("${jwt.refresh-token-validity-in-milliseconds}") long refreshTokenValidity) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.accessTokenValidityTime = accessTokenValidity;
        this.refreshTokenValidityTime = refreshTokenValidity;
    }

    public String createAccessToken(Long userId, String role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenValidityTime);

        String rawRole = (role != null && role.startsWith(ROLE_PREFIX))
                ? role.substring(ROLE_PREFIX.length())
                : role;

        return Jwts.builder()
                .subject(userId.toString())
                .claim(ROLE_CLAIM, rawRole)
                .claim(Constants.TOKEN_TYPE, Constants.ACCESS_TOKEN)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenValidityTime);

        return Jwts.builder()
                .subject(userId.toString())
                .claim(Constants.TOKEN_TYPE, Constants.REFRESH_TOKEN)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaim(token);
        String tokenType = claims.get(Constants.TOKEN_TYPE, String.class);

        if (Constants.REFRESH_TOKEN.equals(tokenType)) {
            throw new BadRequestException(ErrorMessage.NO_REFRESH_TOKEN_IN_LOGIN);
        }

        String roleClaim = claims.get(ROLE_CLAIM, String.class);

        List<SimpleGrantedAuthority> authorities =
                Arrays.stream(StringUtils.hasText(roleClaim) ? roleClaim.split(DELIMITER) : new String[0])
                        .map(String::trim)
                        .filter(StringUtils::hasText)
                        .map(TokenProvider::toRole).filter(Objects::nonNull)
                        .map(SimpleGrantedAuthority::new)
                        .toList();


        return new UsernamePasswordAuthenticationToken(claims.getSubject(), "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEARER.length());
        }

        return null;
    }

    public Claims parseClaim(String token) {
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (SecurityException e) {
            throw new RuntimeException("토큰 복호화에 실패했습니다.");
        }
    }

    private static String toRole(String role) {
        if (!StringUtils.hasText(role)) {
            return null;
        }

        String trimmed = role.trim();
        return trimmed.startsWith(ROLE_PREFIX) ? trimmed : ROLE_PREFIX + trimmed;
    }
}
