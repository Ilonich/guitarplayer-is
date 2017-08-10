package ru.ilonich.igps.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;
import ru.ilonich.igps.exception.ExpiredAuthenticationException;
import ru.ilonich.igps.exception.HmacException;
import ru.ilonich.igps.service.AuthenticationService;
import ru.ilonich.igps.service.SecuredRequestCheckService;
import ru.ilonich.igps.utils.HmacSigner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

import static ru.ilonich.igps.config.security.HmacSecurityFilter.*;
import static ru.ilonich.igps.config.security.misc.SecurityConstants.*;

public class XAuthTokenFilter extends GenericFilterBean {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private AuthenticationService authenticationService;
    private SecuredRequestCheckService checkService;

    XAuthTokenFilter(AuthenticationService authenticationService, SecuredRequestCheckService checkService){
        this.authenticationService = authenticationService;
        this.checkService = checkService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (checkService.canVerify(request)){
            try {
                Cookie jwtCookie = checkService.findJwtCookie(request);
                validateNotNull(jwtCookie, "No jwt cookie found");
                String jwtCookieValue = jwtCookie.getValue();
                validateNotEmpty(jwtCookieValue, "JWT cookies value is missing from the request");
                String login = HmacSigner.getJwtClaim(jwtCookieValue, JWT_CLAIM_LOGIN.toString());
                validateNotNull(login, "No login found in JWT");
                String privateKey = checkService.getPrivateSecret(login);
                validateIsTrue(HmacSigner.verifyJWT(jwtCookieValue, privateKey),"The Json Web Token is invalid");
                validateIsTrue(!HmacSigner.isJwtExpired(jwtCookieValue),"The Json Web Token is expired");
                String csrfHeader = request.getHeader(CSRF_CLAIM_HEADER.toString());
                validateNotNull(csrfHeader, "No csrf header found");
                String jwtCsrf = HmacSigner.getJwtClaim(jwtCookieValue, CSRF_CLAIM_HEADER.toString());
                validateNotNull(jwtCsrf, "No csrf claim found in jwt");
                validateIsTrue(jwtCsrf.equals(csrfHeader), "Invalid CSRF header value");
                this.authenticationService.authenticateByToken(login);
                filterChain.doFilter(request, response);
            } catch (ExpiredAuthenticationException | HmacException | ParseException e) {
                if (checkService.isAuthenticationRequired(request)) {
                    setSecurityFilterExceptionResponse(response, e, request.getRequestURL(), LOG);
                } else {
                    this.authenticationService.authenticateAnonymous();
                    filterChain.doFilter(request, response);
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
