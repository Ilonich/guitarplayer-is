package ru.ilonich.igps.service;

import com.google.common.cache.LoadingCache;
import org.apache.commons.codec.binary.Base64;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ilonich.igps.config.security.misc.KeyPair;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static ru.ilonich.igps.config.security.misc.SecurityConstants.JWT_APP_COOKIE;

@Service("securedRequestCheckService")
public class SecuredRequestCheckServiceImpl implements SecuredRequestCheckService{

    private final LoadingCache<String, KeyPair> keyStore;

    @Autowired
    public SecuredRequestCheckServiceImpl(LoadingCache<String, KeyPair> keyStore) {
        this.keyStore = keyStore;
    }

    @Override
    public boolean canVerify(HttpServletRequest request) {
        return request.getRequestURI().contains("/api") && !request.getRequestURI().contains("/api/authenticate");
    }

    @Override
    public String getPublicSecret(String iss) {
        KeyPair keyPair = keyStore.getIfPresent(iss);
        return keyPair == null ? null : keyPair.getPublicKey();
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
