package ru.ilonich.igps.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


public interface SecuredRequestCheckService {
    /**
     * Check if its possible to verify the request
     * @param request http request
     * @return true if possible, false otherwise
     */
    boolean canVerify(HttpServletRequest request);

    /**
     * Get the stored public secret (locally,remotely,cache,etc..)
     * @param iss issuer
     * @return secret key
     */
    String getPublicSecret(String iss);

    /**
     * Is the secret encoded in base 64
     * @return true if encoded in base 64 , false otherwise
     */
    boolean isSecretInBase64(String secret);

    /**
     * Find a cookie which contain a JWT
     * @param request current http request
     * @return Cookie found, null otherwise
     */
    Cookie findJwtCookie(HttpServletRequest request);
}
