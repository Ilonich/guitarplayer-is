package ru.ilonich.igps.config.security;

import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;
import ru.ilonich.igps.config.security.misc.KeyPair;
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

import static ru.ilonich.igps.config.security.misc.SecurityConstants.*;

public class XAuthTokenFilter extends GenericFilterBean {
    private static final Logger LOG = LoggerFactory.getLogger(XAuthTokenFilter.class);

    private AuthenticationService authenticationService;
    private LoadingCache<String, KeyPair> keyStore;
    private SecuredRequestCheckService checkService;

    public XAuthTokenFilter(AuthenticationService authenticationService, LoadingCache<String, KeyPair> keyStore, SecuredRequestCheckService checkService){
        this.authenticationService = authenticationService;
        this.keyStore = keyStore;
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

                //Cache contains login key?
                KeyPair keyPair = keyStore.getIfPresent(login);
                Assert.notNull(keyPair, "No user found with login: "+login);

                Assert.isTrue(HmacSigner.verifyJWT(jwtCookieValue, keyPair.getPrivateKey()),"The Json Web Token is invalid");

                Assert.isTrue(!HmacSigner.isJwtExpired(jwtCookieValue),"The Json Web Token is expired");

                String csrfHeader = request.getHeader(CSRF_CLAIM_HEADER.toString());
                Assert.notNull(csrfHeader, "No csrf header found");

                String jwtCsrf = HmacSigner.getJwtClaim(jwtCookieValue, CSRF_CLAIM_HEADER.toString());
                Assert.notNull(jwtCsrf, "No csrf claim found in jwt");

                //Check csrf token (prevent csrf attack)
                Assert.isTrue(jwtCsrf.equals(csrfHeader));
                this.authenticationService.authenticateByToken(login);
                filterChain.doFilter(request,response);
            } catch (HmacException | ParseException e) {
                LOG.debug("Token authentication failed", e);
                response.setStatus(403);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
