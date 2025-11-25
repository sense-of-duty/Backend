package org.example.gdgpage.dto.oauth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.gdgpage.domain.auth.PartType;

@Getter
@NoArgsConstructor
public class CompleteProfileRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^010-[0-9]{4}-[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다.")
    private String phone;

    @NotNull(message = "파트 선택은 필수입니다.")
    private PartType partType;
}

