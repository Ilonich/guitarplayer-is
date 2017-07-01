package ru.ilonich.igps.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;
import ru.ilonich.igps.exception.HmacException;
import ru.ilonich.igps.service.AuthenticationService;
import ru.ilonich.igps.service.SecuredRequestCheckService;
import ru.ilonich.igps.to.ErrorInfo;
import ru.ilonich.igps.utils.HmacSigner;
import ru.ilonich.igps.utils.JsonUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static ru.ilonich.igps.config.security.misc.SecurityConstants.*;

public class XAuthTokenFilter extends GenericFilterBean {
    private static final Logger LOG = LoggerFactory.getLogger(XAuthTokenFilter.class);

    private AuthenticationService authenticationService;
    private SecuredRequestCheckService checkService;

    public XAuthTokenFilter(AuthenticationService authenticationService, SecuredRequestCheckService checkService){
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
                Assert.notNull(jwtCookie, "No jwt cookie found");

                String jwtCookieValue = jwtCookie.getValue();
                String login = HmacSigner.getJwtClaim(jwtCookieValue, JWT_CLAIM_LOGIN.toString());
                Assert.notNull(login, "No login found in JWT");

                String privateKey = checkService.getPrivateSecret(login);
                Assert.notNull(privateKey, "No user found with login: "+login);

                Assert.isTrue(HmacSigner.verifyJWT(jwtCookieValue, privateKey),"The Json Web Token is invalid");

                Assert.isTrue(!HmacSigner.isJwtExpired(jwtCookieValue),"The Json Web Token is expired");

                String csrfHeader = request.getHeader(CSRF_CLAIM_HEADER.toString());
                Assert.notNull(csrfHeader, "No csrf header found");

                String jwtCsrf = HmacSigner.getJwtClaim(jwtCookieValue, CSRF_CLAIM_HEADER.toString());
                Assert.notNull(jwtCsrf, "No csrf claim found in jwt");

                //Check csrf token (prevent csrf attack)
                Assert.isTrue(jwtCsrf.equals(csrfHeader));
                this.authenticationService.authenticateByToken(login);
                filterChain.doFilter(request, response);
            } catch (HmacException | ParseException e) {
                if (checkService.isAuthenticationRequired(request)) {
                    LOG.debug("Token authentication failed", e);
                    response.setStatus(403);
                    response.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
                    response.getWriter().write(JsonUtil.writeValue(new ErrorInfo(request.getRequestURL(), "SecurityException", e.getMessage())));
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
