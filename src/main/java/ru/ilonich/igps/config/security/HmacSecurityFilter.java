package ru.ilonich.igps.config.security;

import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;
import ru.ilonich.igps.config.security.misc.HmacToken;
import ru.ilonich.igps.config.security.misc.WrappedRequest;
import ru.ilonich.igps.exception.ExpiredAuthenticationException;
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
import java.text.ParseException;
import java.util.Collections;
import java.util.Map;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static ru.ilonich.igps.config.security.misc.SecurityConstants.*;

public class HmacSecurityFilter extends GenericFilterBean {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public static final Integer JWT_TTL = 60*60*24;
    public static final Map<String, String> CLAIM_WITH_CURRENT_ENCODING = Collections.singletonMap(ENCODING_CLAIM_PROPERTY.toString(), HMAC_SHA_256.toString());

    private SecuredRequestCheckService checkService;

    HmacSecurityFilter(SecuredRequestCheckService checkService) {
        this.checkService = checkService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (checkService.canVerify(request)) {
            try {
                Cookie jwtCookie = checkService.findJwtCookie(request);
                validateNotNull(jwtCookie, "No jwt cookie found");
                String jwtCookieValue = jwtCookie.getValue();
                validateNotEmpty(jwtCookieValue, "JWT cookies value is missing from the request");
                String digestClient = request.getHeader(X_DIGEST.toString());
                validateNotEmpty(digestClient, "The digest is missing from the '" + X_DIGEST.toString() + "' header");
                String xOnceHeader = request.getHeader(X_ONCE.toString());
                validateNotEmpty(xOnceHeader, "The date is missing from the '" + X_ONCE.toString() + "' header");
                StringBuilder urlQuery = new StringBuilder(request.getRequestURL().toString());
                if (request.getQueryString() != null) {
                    urlQuery.append("?").append(URLDecoder.decode(request.getQueryString(), Charsets.UTF_8.displayName()));
                }
                StringBuilder messageDigest = new StringBuilder();
                if ("POST".equals(request.getMethod()) || "PUT".equals(request.getMethod()) || "PATCH".equals(request.getMethod())) {
                    WrappedRequest wrappedRequest = new WrappedRequest(request);
                    messageDigest.append(request.getMethod()).append(wrappedRequest.getBody()).append(urlQuery).append(xOnceHeader);
                } else {
                    messageDigest.append(request.getMethod()).append(urlQuery).append(xOnceHeader);
                }
                //Get Authentication jwt claim
                String iss = HmacSigner.getJwtIss(jwtCookieValue);
                //Digest are calculated using a public shared secret
                String publicSecret = checkService.getPublicSecret(iss);
                String encoding = HmacSigner.getJwtClaim(jwtCookieValue, ENCODING_CLAIM_PROPERTY.toString());
                String digestServer = HmacSigner.encodeMac(publicSecret, messageDigest.toString(), encoding);

                LOG.trace("HMAC JWT: {}\nHMAC url digest: {}\nHMAC Message server: {}\nHMAC Secret server: {}\nHMAC Digest server: {}\nHMAC Digest client: {}\nHMAC encoding: {}",
                        jwtCookieValue, urlQuery.toString(), messageDigest.toString(), publicSecret, digestServer, digestClient, encoding);

                if (digestClient.equals(digestServer)) {
                    LOG.trace("Request is valid, digest are matching");
                    HmacToken publicToken = HmacSigner.getSignedToken(publicSecret, iss, HmacSigner.getTimeLeftSeconds(jwtCookieValue), CLAIM_WITH_CURRENT_ENCODING);
                    response.setHeader(X_TOKEN_ACCESS.toString(), publicToken.getJwt());
                    filterChain.doFilter(request, response);
                } else {
                    throw new HmacException("Digest are not matching! Client: " + digestClient + " / Server: " + digestServer);
                }
            } catch (ExpiredAuthenticationException | HmacException | ParseException e){
                if (checkService.isAuthenticationRequired(request)) {
                    setSecurityFilterExceptionResponse(response, e, request.getRequestURL(), LOG);
                } else {
                    filterChain.doFilter(request, response);
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }

    }

    static void validateNotEmpty(String value, String errorMessage) throws HmacException {
        if (value == null || value.isEmpty()){
            throw new HmacException(errorMessage);
        }
    }

    static void setSecurityFilterExceptionResponse(HttpServletResponse response, Exception e, CharSequence url, Logger log) throws IOException {
        log.debug("Error while validating jwt: ", e);
        if (e instanceof ExpiredAuthenticationException) {
            response.setStatus(401);
            response.getWriter().write(JsonUtil.writeValue(new ErrorInfo(url, "ExpiredAuthenticationException", e.getMessage())));
        } else {
            response.setStatus(403);
            response.getWriter().write(JsonUtil.writeValue(new ErrorInfo(url, "SecurityFilterException", e.getMessage())));
        }
        response.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
    }

    static void validateNotNull(Object value, String errorMessage) throws HmacException {
        if (value == null) throw new HmacException(errorMessage);
    }

    static void validateIsTrue(boolean expression, String errorMessage) throws HmacException {
        if (!expression) throw new HmacException(errorMessage);
    }
}
