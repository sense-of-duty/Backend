package org.example.gdgpage.service.finder;

import org.example.gdgpage.domain.auth.User;

public interface FindUser {
    Long getUserIdFromRefreshToken(String refreshToken);
    User getUserById(Long userId);
}
