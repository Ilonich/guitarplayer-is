package ru.ilonich.igps.service;

import ru.ilonich.igps.exception.ExpiredAuthenticationException;

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
     * Check request is for anonymous users too
     * @param request http request
     * @return true if required, false otherwise
     */
    boolean isAuthenticationRequired(HttpServletRequest request);

    /**
     * Get the stored public secret (locally,remotely,cache,etc..)
     * @param iss issuer
     * @return secret key
     */
    String getPublicSecret(Integer iss) throws ExpiredAuthenticationException;

    /**
     * Get the stored private secret (locally,remotely,cache,etc..)
     * @param iss issuer
     * @return secret key
     */
    String getPrivateSecret(Integer iss) throws ExpiredAuthenticationException;

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
