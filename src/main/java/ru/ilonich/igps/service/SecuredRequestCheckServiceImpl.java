package ru.ilonich.igps.service;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ilonich.igps.exception.ExpiredAuthenticationException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static ru.ilonich.igps.config.security.misc.SecurityConstants.JWT_APP_COOKIE;

@Service("securedRequestCheckService")
public class SecuredRequestCheckServiceImpl implements SecuredRequestCheckService{

    private final LoginSecretKeysPairStoreService keysStoreService;

    @Autowired
    public SecuredRequestCheckServiceImpl(LoginSecretKeysPairStoreService keysStoreService) {
        this.keysStoreService = keysStoreService;
    }

    @Override
    public boolean canVerify(HttpServletRequest request) {
        return request.getRequestURI().contains("/api") &&
                !(request.getRequestURI().contains("/api/validate") ||
                        request.getRequestURI().contains("/api/reset") ||
                        request.getRequestURI().contains("/api/confirm") ||
                        request.getRequestURI().contains("/api/authenticate") ||
                        request.getRequestURI().contains("/api/register"));
    }

    @Override
    public boolean isAuthenticationRequired(HttpServletRequest request) {
        return !request.getRequestURI().contains("/api/public");
    }

    @Override
    public String getPublicSecret(String iss) throws ExpiredAuthenticationException {
        return keysStoreService.get(iss).getPublicKey();
    }

    @Override
    public String getPrivateSecret(String iss) throws ExpiredAuthenticationException {
        return keysStoreService.get(iss).getPrivateKey();
    }

    @Override
    public boolean isSecretInBase64(String secret) {
        return Base64.isArrayByteBase64(secret.getBytes());
    }

    @Override
    public Cookie findJwtCookie(HttpServletRequest request) {
        if(request.getCookies() == null || request.getCookies().length == 0) {
            return null;
        }
        for(Cookie cookie : request.getCookies()) {
            if(cookie.getName().contains(JWT_APP_COOKIE.toString())) {
                return cookie;
            }
        }
        return null;
    }
}
