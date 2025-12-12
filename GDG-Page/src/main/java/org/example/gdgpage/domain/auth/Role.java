package org.example.gdgpage.domain.auth;

public enum Role {
    MEMBER,
    CORE,
    ORGANIZER;

    public boolean isAdmin() {
        return this == CORE || this == ORGANIZER;
    }
}
