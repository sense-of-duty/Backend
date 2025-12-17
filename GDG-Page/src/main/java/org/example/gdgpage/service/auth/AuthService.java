package org.example.gdgpage.service.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.domain.auth.OAuthAccount;
import org.example.gdgpage.domain.auth.Provider;
import org.example.gdgpage.domain.auth.User;
import org.example.gdgpage.domain.refresh.RefreshToken;
import org.example.gdgpage.dto.auth.request.LoginRequest;
import org.example.gdgpage.dto.auth.request.SignUpRequest;
import org.example.gdgpage.dto.auth.response.LoginResponse;
import org.example.gdgpage.dto.oauth.request.CompleteProfileRequest;
import org.example.gdgpage.dto.oauth.request.OAuthLoginRequest;
import org.example.gdgpage.dto.oauth.response.GoogleTokenResponse;
import org.example.gdgpage.dto.oauth.response.GoogleUserInfoResponse;
import org.example.gdgpage.dto.token.TokenDto;
import org.example.gdgpage.dto.user.response.UserResponse;
import org.example.gdgpage.exception.BadRequestException;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.jwt.TokenProvider;
import org.example.gdgpage.mapper.auth.LoginMapper;
import org.example.gdgpage.mapper.auth.UserMapper;
import org.example.gdgpage.repository.auth.OAuthAccountRepository;
import org.example.gdgpage.repository.auth.RefreshTokenRepository;
import org.example.gdgpage.repository.auth.UserRepository;
import org.example.gdgpage.service.finder.FindUser;
import org.example.gdgpage.util.CookieUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuthAccountRepository oAuthAccountRepository;
    private final FindUser findUser;
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
    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(() -> new BadRequestException(ErrorMessage.WRONG_EMAIL_INPUT));

        if (user.isDeleted()) {
            throw new BadRequestException(ErrorMessage.DELETED_USER);
        }

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new BadRequestException(ErrorMessage.WRONG_PASSWORD_INPUT);
        }

        return updateTimeAndCreateToken(user, httpServletResponse);
    }

    @Transactional
    public LoginResponse oauthLogin(OAuthLoginRequest oAuthLoginRequest, HttpServletResponse httpServletResponse) {

        GoogleTokenResponse tokenResponse = googleOAuthClient.exchangeCodeForToken(oAuthLoginRequest.authorizationCode());
        GoogleUserInfoResponse userInfo = googleOAuthClient.getUserInfo(tokenResponse.accessToken());

        if (userInfo.verifiedEmail() != null && !userInfo.verifiedEmail()) {
            throw new BadRequestException(ErrorMessage.OAUTH_EMAIL_NOT_VERIFIED);
        }

        String email = userInfo.email();
        String providerId = userInfo.id();

        OAuthAccount oauthAccount = oAuthAccountRepository.findByProviderAndProviderId(Provider.GOOGLE, providerId).orElse(null);

        User user;

        boolean hasProfileImage = userInfo.picture() != null && !userInfo.picture().isBlank();

        if (oauthAccount != null) {
            user = oauthAccount.getUser();

            if (user.isDeleted()) {
                throw new BadRequestException(ErrorMessage.DELETED_USER);
            }

            oauthAccount.updateLastLoginAt(LocalDateTime.now());

            if (hasProfileImage) {
                user.updateProfileImage(userInfo.picture());
            }

        } else {
            user = userRepository.findByEmail(email).orElse(null);

            if (user != null) {
                throw new BadRequestException(ErrorMessage.EMAIL_ALREADY_REGISTERED_WITH_OTHER_PROVIDER);
            }

            user = userRepository.save(User.createOAuthUser(email, userInfo.name()));

            if (hasProfileImage) {
                user.updateProfileImage(userInfo.picture());
            }

            oAuthAccountRepository.save(OAuthAccount.create(user, Provider.GOOGLE, providerId, email));
        }

        return updateTimeAndCreateToken(user, httpServletResponse);
    }


    @Transactional
    public TokenDto reissue(String refreshToken, HttpServletResponse httpServletResponse) {
        Long userId = findUser.getUserIdFromRefreshToken(refreshToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_USER));

        String newAccessToken = tokenProvider.createAccessToken(user.getId(), user.getRole().name());

        refreshTokenRepository.deleteByRefreshToken(refreshToken);

        String newRefreshToken = tokenProvider.createRefreshToken(user.getId());

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(user.getId())
                        .refreshToken(newRefreshToken)
                        .build()
        );

        CookieUtil.setRefreshTokenCookie(httpServletResponse, newRefreshToken);

        return new TokenDto(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(String refreshToken, HttpServletResponse response) {
        if (StringUtils.hasText(refreshToken)) {
            refreshTokenRepository.deleteByRefreshToken(refreshToken);
        }

        CookieUtil.clearRefreshTokenCookie(response);
    }

    @Transactional
    public UserResponse completeProfile(CompleteProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException(ErrorMessage.NEED_TO_LOGIN);
        }

        Long userId = Long.parseLong(authentication.getName());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_USER));

        if (user.isProfileCompleted()) {
            throw new BadRequestException(ErrorMessage.ALREADY_PROFILE_COMPLETED);
        }

        if (userRepository.existsByPhone(request.phone())) {
            throw new BadRequestException(ErrorMessage.ALREADY_EXIST_PHONE);
        }

        user.completeProfile(request.name(), request.phone(), request.partType());

        return UserMapper.toUserResponse(user);
    }

    private LoginResponse updateTimeAndCreateToken(User user, HttpServletResponse httpServletResponse) {
        user.updateLastLogin(LocalDateTime.now());

        String accessToken = tokenProvider.createAccessToken(user.getId(), user.getRole().name());
        String refreshToken = tokenProvider.createRefreshToken(user.getId());

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(user.getId())
                        .refreshToken(refreshToken)
                        .build()
        );

        CookieUtil.setRefreshTokenCookie(httpServletResponse, refreshToken);
        TokenDto tokenDto = new TokenDto(accessToken, refreshToken);
        UserResponse userResponse = UserMapper.toUserResponse(user);

        return LoginMapper.of(tokenDto, userResponse);
    }
}
