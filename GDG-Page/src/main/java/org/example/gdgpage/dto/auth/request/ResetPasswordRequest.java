package org.example.gdgpage.dto.auth.request;

public record ResetPasswordRequest(
        String token,
        String newPassword,
        String confirmPassword
) {}
