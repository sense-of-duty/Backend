package org.example.gdgpage.config;

import lombok.RequiredArgsConstructor;
import org.example.gdgpage.common.Constants;
import org.example.gdgpage.exception.ErrorMessage;
import org.example.gdgpage.jwt.JwtFilter;
import org.example.gdgpage.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity(debug = true) // 개발 완료 후 삭제 요망
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;

    @Value("#{'${app.cors.allowed-origin-patterns}'.split(',')}")
    private List<String> allowedOrigins;

    private static final String[] PUBLIC_AUTH = {
            "/auth/signup",
            "/auth/login",
            "/auth/oauth/login",
            "/auth/reissue",
            "/auth/logout",
            "/oauth2/**",
            "/auth/resend-verification",
            "/auth/verify-email/**",
            "/photos/**"
    };

    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .authorizeHttpRequests(a -> a
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .requestMatchers(PUBLIC_AUTH).permitAll()
                        .requestMatchers("/api/free-posts/admin").hasAnyRole("ORGANIZER", "CORE")
                        .requestMatchers("/admin/**").hasAnyRole("ORGANIZER")
                        .anyRequest().authenticated()
                )
                .headers(h -> h.frameOptions(Customizer.withDefaults()).disable())
                .addFilterBefore(new JwtFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(allowedOrigins);
        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization","Set-Cookie"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (rehttpServletRequest, httpServletResponse, authenticationException) -> {
            httpServletResponse.setStatus(401);
            httpServletResponse.setContentType(Constants.CONTENT_TYPE);
            httpServletResponse.getWriter().write(Constants.MESSAGE_INTRO + ErrorMessage.NEED_TO_LOGIN.getMessage() + Constants.MESSAGE_OUTRO);
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (httpServletRequest, httpServletResponse, accessDeniedException) -> {
            httpServletResponse.setStatus(403);
            httpServletResponse.setContentType(Constants.CONTENT_TYPE);
            httpServletResponse.getWriter().write(Constants.MESSAGE_INTRO + ErrorMessage.ACCESS_DENY.getMessage() + Constants.MESSAGE_OUTRO);
        };
    }
}
