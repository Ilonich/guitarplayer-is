package ru.ilonich.igps.model;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;

import static java.util.Objects.requireNonNull;

public class AuthenticatedUser implements UserDetails, CredentialsContainer {

    private User user;

    public AuthenticatedUser(User user) {
        this.user = user;
    }

    public static AuthenticatedUser safeGet() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        return (principal instanceof AuthenticatedUser) ? (AuthenticatedUser) principal : null;
    }

    public static AuthenticatedUser get() {
        AuthenticatedUser user = safeGet();
        requireNonNull(user, "No authorized user found");
        return user;
    }

    public static int id() {
        return get().getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void eraseCredentials() {}

    public boolean isEmailVerificated() {
        return user.isEnabled();
    }

    public boolean isBanned() {
        return !user.isDecent();
    }

    public int getId() {
        return user.getId();
    }

    public User getUser() {
        return user;
    }
}
