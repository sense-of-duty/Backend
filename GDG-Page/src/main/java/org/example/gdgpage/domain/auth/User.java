package org.example.gdgpage.domain.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(nullable = false, unique = true, length = 64)
    private String email;

    @Column(length = 64)
    @Size(min = 8, max = 64)
    private String password;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(nullable = false, length = 20)
    @Pattern(regexp = "^010-[0-9]{4}-[0-9]{4}$")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private PartType part;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role;

    // 관리자에게 승인됐는지 여부
    @Column(name = "is_approved", nullable = false)
    private boolean isApproved;

    // 로컬은 항상 true, 소셜 로그인은 추가 정보 입력 페이지에서 입력이 완료되면 true
    @Column(name = "is_profile_completed", nullable = false)
    private boolean isProfileCompleted;

    // 현재 멤버인지, 전 기수 멤버인지
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column
    private LocalDateTime lastLoginAt;

    public static User createUser(String email, String password, String name, String phone, PartType part) {
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .phone(phone)
                .part(part)
                .role(Role.MEMBER)
                .isApproved(false)
                .isProfileCompleted(true)
                .build();
    }

    public static User createOAuthUser(String email, String name, OAuthProvider provider) {
        return User.builder()
                .email(email)
                .password(null)
                .name(name)
                .phone(null)
                .part(null)
                .role(Role.MEMBER)
                .isApproved(false)
                .isProfileCompleted(false)
                .build();
    }

    public void completeProfile(String phone, PartType part) {
        this.phone = phone;
        this.part = part;
        this.isProfileCompleted = true;
    }
}
