package org.example.gdgpage.service.auth;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.common.Constants;
import org.example.gdgpage.domain.auth.OAuthAccount;
import org.example.gdgpage.domain.auth.Provider;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.dto.auth.request.LoginRequest;
import org.example.gdgpage.dto.auth.request.SignUpRequest;
import org.example.gdgpage.dto.auth.response.LoginResponse;
import org.example.gdgpage.dto.auth.response.UserResponse;
import org.example.gdgpage.dto.oauth.request.OAuthLoginRequest;
import org.example.gdgpage.dto.oauth.response.GoogleTokenResponse;
import org.example.gdgpage.dto.oauth.response.GoogleUserInfoResponse;
import org.example.gdgpage.dto.token.TokenDto;
import org.example.gdgpage.dto.token.request.RefreshTokenRequest;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.jwt.TokenProvider;
import org.example.gdgpage.mapper.UserMapper;
import org.example.gdgpage.repository.OAuthAccountRepository;
import org.example.gdgpage.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OAuthAccountRepository oAuthAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final GoogleOAuthClient googleOAuthClient;

    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.email())) {
            throw new BadRequestException(ErrorMessage.ALREADY_EXIST_EMAIL);
        }

        if (!signUpRequest.password().equals(signUpRequest.confirmPassword())) {
            throw new BadRequestException(ErrorMessage.WRONG_CHECK_PASSWORD);
        }

        if (userRepository.existsByPhone(signUpRequest.phone())) {
            throw new BadRequestException(ErrorMessage.ALREADY_EXIST_PHONE);
        }

        String encodedPassword = passwordEncoder.encode(signUpRequest.password());

        User user = User.createUser(signUpRequest.email(), encodedPassword, signUpRequest.name(), signUpRequest.phone(), signUpRequest.partType());

        userRepository.save(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(() -> new BadRequestException(ErrorMessage.WRONG_EMAIL_INPUT));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new BadRequestException(ErrorMessage.WRONG_PASSWORD_INPUT);
        }

        return updateTimeAndCreateToken(user);
    }

    private LoginResponse updateTimeAndCreateToken(User user) {
        user.updateLastLogin(LocalDateTime.now());

        String accessToken = tokenProvider.createAccessToken(user.getId(), user.getRole().name());
        String refreshToken = tokenProvider.createRefreshToken(user.getId());

        TokenDto tokenDto = new TokenDto(accessToken, refreshToken);
        UserResponse userResponse = UserMapper.toUserResponse(user);

        return LoginResponse.of(tokenDto, userResponse);
    }

    @Transactional
    public LoginResponse oauthLogin(OAuthLoginRequest oAuthLoginRequest) {

        GoogleTokenResponse tokenResponse = googleOAuthClient.exchangeCodeForToken(oAuthLoginRequest.authorizationCode());
        GoogleUserInfoResponse userInfo = googleOAuthClient.getUserInfo(tokenResponse.getAccessToken());

        if (userInfo.getVerifiedEmail() != null && !userInfo.getVerifiedEmail()) {
            throw new BadRequestException(ErrorMessage.OAUTH_EMAIL_NOT_VERIFIED);
        }

        String email = userInfo.getEmail();
        String providerId = userInfo.getId();

        OAuthAccount oauthAccount = oAuthAccountRepository.findByProviderAndProviderId(Provider.GOOGLE, providerId).orElse(null);

        User user;

        if (oauthAccount != null) {
            user = oauthAccount.getUser();
            oauthAccount.updateLastLoginAt(LocalDateTime.now());
        } else {
            user = userRepository.findByEmail(email).orElse(null);

            if (user != null) {
                throw new BadRequestException(ErrorMessage.EMAIL_ALREADY_REGISTERED_WITH_OTHER_PROVIDER);
            }

            user = userRepository.save(User.createOAuthUser(email, userInfo.getName()));

            oAuthAccountRepository.save(OAuthAccount.create(user, Provider.GOOGLE, providerId, email));
        }

        return updateTimeAndCreateToken(user);
    }

    @Transactional
    public TokenDto reissue(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException(ErrorMessage.INVALID_TOKEN);
        }

        Claims claims = tokenProvider.parseClaim(refreshToken);
        String tokenType = claims.get(Constants.TOKEN_TYPE, String.class);

        if (!Constants.REFRESH_TOKEN.equals(tokenType)) {
            throw new BadRequestException(ErrorMessage.INVALID_TOKEN);
        }

        Long userId = Long.parseLong(claims.getSubject());
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_USER));

        String newAccessToken = tokenProvider.createAccessToken(user.getId(), user.getRole().name());

        return new TokenDto(newAccessToken, refreshToken);
    }
}
