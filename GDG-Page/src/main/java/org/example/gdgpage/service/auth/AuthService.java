package org.example.gdgpage.service.auth;

import lombok.RequiredArgsConstructor;
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
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException(ErrorMessage.ALREADY_EXIST_EMAIL);
        }

        if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
            throw new BadRequestException(ErrorMessage.WRONG_CHECK_PASSWORD);
        }

        if (userRepository.existsByPhone(signUpRequest.getPhone())) {
            throw new BadRequestException(ErrorMessage.ALREADY_EXIST_PHONE);
        }

        String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());

        User user = User.createUser(
                signUpRequest.getEmail(),
                encodedPassword,
                signUpRequest.getName(),
                signUpRequest.getPhone(),
                signUpRequest.getPartType()
        );

        userRepository.save(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.WRONG_EMAIL_INPUT));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
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

        GoogleTokenResponse tokenResponse = googleOAuthClient.exchangeCodeForToken(oAuthLoginRequest.getAuthorizationCode());

        GoogleUserInfoResponse userInfo = googleOAuthClient.getUserInfo(tokenResponse.getAccessToken());

        if (userInfo.getVerifiedEmail() != null && !userInfo.getVerifiedEmail()) {
            throw new BadRequestException(ErrorMessage.OAUTH_EMAIL_NOT_VERIFIED);
        }

        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> {
                    User newUser = User.createOAuthUser(userInfo.getEmail(), userInfo.getName());
                    return userRepository.save(newUser);
                });

        oAuthAccountRepository
                .findByProviderAndProviderId(Provider.GOOGLE, userInfo.getId())
                .orElseGet(() -> oAuthAccountRepository.save(
                        OAuthAccount.create(
                                user,
                                Provider.GOOGLE,
                                userInfo.getId(),
                                userInfo.getEmail()
                        )
                ));

        return updateTimeAndCreateToken(user);
    }
}
