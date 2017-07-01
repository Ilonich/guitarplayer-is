package ru.ilonich.igps.model;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import ru.ilonich.igps.model.enumerations.Role;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public final class AnonymousUser implements Authentication {

    public static final AnonymousUser ANONYMOUS_TOKEN = new AnonymousUser();

    private final String mock = "Anonymous";
    private final Set<GrantedAuthority> authorities = Collections.singleton(Role.ANONYMOUS);

    private AnonymousUser(){}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return mock;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return mock;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalArgumentException("Anonymous user always is authenticated");
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof AnonymousUser;
    }

    @Override
    public String toString() {
        return mock;
    }

    @Override
    public int hashCode() {
        return mock.hashCode();
    }

    @Override
    public String getName() {
        return mock;
    }
}
