package ru.ilonich.igps.model.enumerations;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ANONYMOUS,
    ADMIN,
    MODERATOR,
    CONFIDANT,
    USER;

    @Override
    public String getAuthority() {
        return name();
    }
}
