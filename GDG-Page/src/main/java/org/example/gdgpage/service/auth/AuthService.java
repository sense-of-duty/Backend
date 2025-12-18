package org.example.gdgpage.service.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.gdgpage.common.Constants;
import org.example.gdgpage.domain.auth.AuthUser;
import org.example.gdgpage.domain.auth.EmailVerificationToken;
import org.example.gdgpage.domain.auth.OAuthAccount;
import org.example.gdgpage.domain.auth.PasswordResetToken;
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
import org.example.gdgpage.exception.UnauthorizedException;
import org.example.gdgpage.jwt.TokenProvider;
import org.example.gdgpage.mapper.LoginMapper;
import org.example.gdgpage.mapper.UserMapper;
import org.example.gdgpage.repository.EmailVerificationTokenRepository;
import org.example.gdgpage.repository.OAuthAccountRepository;
import org.example.gdgpage.repository.PasswordResetTokenRepository;
import org.example.gdgpage.repository.RefreshTokenRepository;
import org.example.gdgpage.repository.UserRepository;
import org.example.gdgpage.service.mail.EmailService;
import org.example.gdgpage.util.CookieUtil;
import org.example.gdgpage.util.DeviceCookieUtil;
import org.example.gdgpage.util.TokenHashUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuthAccountRepository oAuthAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final GoogleOAuthClient googleOAuthClient;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Value("${password-reset.frontend-reset-page-url}")
    private String frontendBaseUrl;

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
        createAndSendEmailVerification(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest loginRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(() -> new BadRequestException(ErrorMessage.WRONG_EMAIL_INPUT));

        if (user.isDeleted()) {
            throw new BadRequestException(ErrorMessage.DELETED_USER);
        }

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new BadRequestException(ErrorMessage.WRONG_PASSWORD_INPUT);
        }

        if (!user.isEmailVerified()) {
            throw new BadRequestException(ErrorMessage.EMAIL_NOT_VERIFIED);
        }

        String deviceId = DeviceCookieUtil.getOrSetDeviceId(httpServletRequest, httpServletResponse);

        user.updateLastLogin(LocalDateTime.now());

        return getLoginResponse(httpServletResponse, user, deviceId);
    }

    @Transactional
    public LoginResponse oauthLogin(OAuthLoginRequest oAuthLoginRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
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

        return updateTimeAndCreateToken(user, httpServletRequest, httpServletResponse);
    }

    @Transactional
    public TokenDto reissue(String refreshToken, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
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

        String tokenId = claims.get(Constants.JTI, String.class);

        String deviceId = DeviceCookieUtil.getOrSetDeviceId(httpServletRequest, httpServletResponse);

        RefreshToken stored = refreshTokenRepository.findByUserIdAndTokenId(userId, tokenId)
                .orElseThrow(() -> new UnauthorizedException(ErrorMessage.INVALID_TOKEN));

        if (stored.isExpired() || stored.isRevoked()) {
            throw new UnauthorizedException(ErrorMessage.INVALID_TOKEN);
        }

        String presentedHash = TokenHashUtil.sha256Base64(refreshToken);

        if (!presentedHash.equals(stored.getTokenHash())) {
            refreshTokenRepository.revokeAllActiveByUser(userId);
            CookieUtil.clearRefreshTokenCookie(httpServletResponse);

            throw new UnauthorizedException(ErrorMessage.INVALID_TOKEN);
        }

        String newAccessToken = tokenProvider.createAccessToken(userId, userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_USER))
                .getRole().name()
        );

        String newRefreshToken = tokenProvider.createRefreshToken(userId, deviceId);
        Claims newClaims = tokenProvider.parseClaim(newRefreshToken);
        String newTokenId = newClaims.get(Constants.JTI, String.class);
        Date newExp = newClaims.getExpiration();

        stored.revoke(newTokenId);
        stored.markUsed();

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(userId)
                        .deviceId(deviceId)
                        .tokenId(newTokenId)
                        .tokenHash(TokenHashUtil.sha256Base64(newRefreshToken))
                        .expiresAt(newExp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                        .revoked(false)
                        .lastUsedAt(LocalDateTime.now())
                        .build()
        );

        CookieUtil.setRefreshTokenCookie(httpServletResponse, newRefreshToken);

        return new TokenDto(newAccessToken);
    }

    @Transactional
    public void logout(String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        String deviceId = DeviceCookieUtil.getOrSetDeviceId(request, response);

        if (StringUtils.hasText(refreshToken) && tokenProvider.validateToken(refreshToken)) {
            Claims claims = tokenProvider.parseClaim(refreshToken);
            Long userId = Long.parseLong(claims.getSubject());

            refreshTokenRepository.revokeAllActiveByUserAndDevice(userId, deviceId);
        }

        CookieUtil.clearRefreshTokenCookie(response);
    }

    @Transactional
    public UserResponse completeProfile(CompleteProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new BadRequestException(ErrorMessage.NEED_TO_LOGIN);
        }

        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        Long userId = authUser.id();

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

    private LoginResponse updateTimeAndCreateToken(User user, HttpServletRequest request, HttpServletResponse response) {
        user.updateLastLogin(LocalDateTime.now());

        String deviceId = DeviceCookieUtil.getOrSetDeviceId(request, response);

        return getLoginResponse(response, user, deviceId);
    }

    @Transactional
    public void verifyEmail(String token) {
        EmailVerificationToken emailToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_EMAIL_VERIFICATION_TOKEN));

        if (emailToken.isUsed()) {
            throw new BadRequestException(ErrorMessage.INVALID_EMAIL_VERIFICATION_TOKEN);
        }

        if (emailToken.isExpired()) {
            throw new BadRequestException(ErrorMessage.EXPIRED_EMAIL_VERIFICATION_TOKEN);
        }

        User user = userRepository.findById(emailToken.getUserId())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_USER));

        if (user.isEmailVerified()) {
            throw new BadRequestException(ErrorMessage.EMAIL_ALREADY_VERIFIED);
        }

        user.verifyEmail();
        emailToken.markUsed();
    }

    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_USER));

        String token = java.util.UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .userId(user.getId())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        String resetLink = frontendBaseUrl + "/auth/reset-password?token=" + token;
        emailService.sendPasswordResetMail(user.getEmail(), resetLink);
    }

    @Transactional
    public void resetPassword(String token, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException(ErrorMessage.WRONG_CHECK_PASSWORD);
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_PASSWORD_RESET_TOKEN));

        if (resetToken.isUsed()) {
            throw new BadRequestException(ErrorMessage.INVALID_PASSWORD_RESET_TOKEN);
        }

        if (resetToken.isExpired()) {
            throw new BadRequestException(ErrorMessage.EXPIRED_PASSWORD_RESET_TOKEN);
        }

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_USER));

        String encoded = passwordEncoder.encode(newPassword);
        user.updatePassword(encoded);

        resetToken.markUsed();

        refreshTokenRepository.deleteAllByUserId(user.getId());
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_USER));

        if (user.isEmailVerified()) {
            throw new BadRequestException(ErrorMessage.EMAIL_ALREADY_VERIFIED);
        }

        emailVerificationTokenRepository.deleteAllByUserId(user.getId());

        createAndSendEmailVerification(user);
    }

    @Transactional(readOnly = true)
    public boolean getEmailVerificationStatus(String email) {
        return userRepository.findByEmail(email)
                .map(User::isEmailVerified)
                .orElse(false);
    }

    private void createAndSendEmailVerification(User user) {
        String token = java.util.UUID.randomUUID().toString();

        EmailVerificationToken emailToken = EmailVerificationToken.builder()
                .token(token)
                .userId(user.getId())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();

        emailVerificationTokenRepository.save(emailToken);

        String verificationLink = frontendBaseUrl + "/auth/verify-email?token=" + token;
        emailService.sendEmailVerificationMail(user.getEmail(), verificationLink);
    }


    private LoginResponse getLoginResponse(HttpServletResponse httpServletResponse, User user, String deviceId) {
        String accessToken = tokenProvider.createAccessToken(user.getId(), user.getRole().name());
        String refreshToken = tokenProvider.createRefreshToken(user.getId(), deviceId);

        Claims claims = tokenProvider.parseClaim(refreshToken);
        String tokenId = claims.get(Constants.JTI, String.class);
        Date exp = claims.getExpiration();

        refreshTokenRepository.revokeAllActiveByUserAndDevice(user.getId(), deviceId);

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(user.getId())
                        .deviceId(deviceId)
                        .tokenId(tokenId)
                        .tokenHash(TokenHashUtil.sha256Base64(refreshToken))
                        .expiresAt(exp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                        .revoked(false)
                        .lastUsedAt(LocalDateTime.now())
                        .build()
        );

        CookieUtil.setRefreshTokenCookie(httpServletResponse, refreshToken);

        return LoginMapper.of(new TokenDto(accessToken), UserMapper.toUserResponse(user));
    }
}
