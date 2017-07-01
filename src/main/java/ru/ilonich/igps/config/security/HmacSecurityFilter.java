package ru.ilonich.igps.config.security;

import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;
import ru.ilonich.igps.config.security.misc.HmacToken;
import ru.ilonich.igps.config.security.misc.WrappedRequest;
import ru.ilonich.igps.exception.HmacException;
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
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Map;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static ru.ilonich.igps.config.security.misc.SecurityConstants.*;

public class HmacSecurityFilter extends GenericFilterBean {
    private static final Logger LOG = LoggerFactory.getLogger(HmacSecurityFilter.class);

    public static final Integer JWT_TTL = 60*60*24;

    private SecuredRequestCheckService checkService;

    public HmacSecurityFilter(SecuredRequestCheckService checkService) {
        this.checkService = checkService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        WrappedRequest wrappedRequest = new WrappedRequest(request);
        try {
            if (checkService.canVerify(request)) {
                //Get Authentication jwt claim
                Cookie jwtCookie = checkService.findJwtCookie(request);
                Assert.notNull(jwtCookie,"No jwt cookie found");
                String jwtCookieValue = jwtCookie.getValue();
                if (jwtCookieValue == null || jwtCookieValue.isEmpty()) {
                    throw new HmacException("JWT cookies value is missing from the request");
                }
                String digestClient = request.getHeader(X_DIGEST.toString());
                if (digestClient == null || digestClient.isEmpty()) {
                    throw new HmacException("The digest is missing from the '" + X_DIGEST.toString() + "' header");
                }
                String xOnceHeader = request.getHeader(X_ONCE.toString());
                if (xOnceHeader == null || xOnceHeader.isEmpty()) {
                    throw new HmacException("The date is missing from the '" + X_ONCE.toString() + "' header");
                }
                String url = request.getRequestURL().toString();
                //just in case (should not be used for restful)
                if (request.getQueryString() != null) {
                    url += "?" + URLDecoder.decode(request.getQueryString(), Charsets.UTF_8.displayName());
                }
                String encoding = HmacSigner.getJwtClaim(jwtCookieValue, ENCODING_CLAIM_PROPERTY.toString());
                String iss = HmacSigner.getJwtIss(jwtCookieValue);
                String publicSecret = checkService.getPublicSecret(iss);
                Assert.notNull(publicSecret, "Secret key is missing from the keys storage (logout)");
                String message;
                if ("POST".equals(request.getMethod()) || "PUT".equals(request.getMethod()) || "PATCH".equals(request.getMethod())) {
                    message = request.getMethod().concat(wrappedRequest.getBody()).concat(url).concat(xOnceHeader);
                } else {
                    message = request.getMethod().concat(url).concat(xOnceHeader);
                }

                //Digest are calculated using a public shared secret
                String digestServer = HmacSigner.encodeMac(publicSecret, message, encoding);

                LOG.info("\nHMAC JWT: {}\nHMAC url digest: {}\nHMAC Message server: {}\nHMAC Secret server: {}\nHMAC Digest server: {}\nHMAC Digest client: {}\nHMAC encoding: {}",
                        jwtCookieValue, url, message, publicSecret, digestServer, digestClient, encoding);

                if (digestClient.equals(digestServer)) {
                    LOG.debug("Request is valid, digest are matching");
                    Map<String,String> encodingClaim = Collections.singletonMap(ENCODING_CLAIM_PROPERTY.toString(), HMAC_SHA_256.toString());
                    HmacToken publicToken = HmacSigner.getSignedToken(publicSecret, iss, HmacSigner.getTimeLeftSeconds(jwtCookieValue), encodingClaim);
                    response.setHeader(X_TOKEN_ACCESS.toString(), publicToken.getJwt());
                    filterChain.doFilter(wrappedRequest, response);
                } else {
                    throw new HmacException("Digest are not matching! Client: " + digestClient + " / Server: " + digestServer);
                }
            } else {
                filterChain.doFilter(wrappedRequest, response);
            }
        } catch (Exception e){
            if (checkService.isAuthenticationRequired(request)) {
                LOG.debug("Error while generating hmac token", e);
                response.setStatus(403);
                response.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
                response.getWriter().write(JsonUtil.writeValue(new ErrorInfo(wrappedRequest.getRequestURL(), "SecurityException", e.getMessage())));
            } else {
                filterChain.doFilter(wrappedRequest, response);
            }
        }
    }
}
