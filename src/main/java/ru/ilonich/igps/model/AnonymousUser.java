package ru.ilonich.igps.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import ru.ilonich.igps.model.enumerations.Role;

import java.util.Collections;

public final class AnonymousUser {
    private static final String NAME = "Anonymous";
    private static final User MOCK_USER = new User(1, null, null, false, false, NAME, null, 0, 0, null, null, null);
    public static final Authentication ANONYMOUS_TOKEN = new AnonymousAuthenticationToken(MOCK_USER);

    private static class AnonymousAuthenticationToken extends AbstractAuthenticationToken {
        private static final long serialVersionUID = 123456L;

        private final Object principal;

        private AnonymousAuthenticationToken(User mock) {
            super(Collections.singletonList(Role.ANONYMOUS));
            this.principal = mock;
            super.setAuthenticated(true);
        }

        @Override
        public Object getCredentials() {
            return "";
        }

        @Override
        public Object getPrincipal() {
            return principal;
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            throw new IllegalArgumentException("Anonymous user is authenticated by default, but has limited permissions");
        }

        @Override
        public void eraseCredentials(){}
    }
}
