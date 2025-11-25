package org.example.gdgpage.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.gdgpage.domain.auth.PartType;

@Getter
@NoArgsConstructor
public class SignUpRequestDTO {

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 64, message = "비밀번호는 8자 이상, 64자 이하여야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String confirmPassword;

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 10)
    private String name;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^010-[0-9]{4}-[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다.")
    private String phone;

    @NotNull(message = "파트 선택은 필수입니다.")
    private PartType partType;
}
