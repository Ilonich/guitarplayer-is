package ru.ilonich.igps.config.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import ru.ilonich.igps.exception.ExpiredAuthenticationException;
import ru.ilonich.igps.exception.HmacException;
import ru.ilonich.igps.service.SecuredRequestCheckService;
import ru.ilonich.igps.service.cacheonly.WebSocketSessionsService;
import ru.ilonich.igps.utils.HmacSigner;

import java.text.ParseException;

import static org.springframework.http.HttpHeaders.COOKIE;
import static ru.ilonich.igps.config.security.misc.SecurityConstants.CSRF_CLAIM_HEADER;
import static ru.ilonich.igps.config.security.misc.SecurityConstants.JWT_APP_COOKIE;
import static ru.ilonich.igps.config.security.misc.SecurityConstants.JWT_CLAIM_LOGIN;

public class CheckJwtCookiesWebSocketHandlerDecoratorFactory implements WebSocketHandlerDecoratorFactory {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private SecuredRequestCheckService checkService;
    private WebSocketSessionsService sessionsService;

    CheckJwtCookiesWebSocketHandlerDecoratorFactory(SecuredRequestCheckService checkService, WebSocketSessionsService sessionsService) {
        this.checkService = checkService;
        this.sessionsService = sessionsService;
    }

    @Override
    public WebSocketHandler decorate(WebSocketHandler handler) {
        return new WebSocketHandlerDecorator(handler) {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                try {
                    String cookie = session.getHandshakeHeaders().get(COOKIE).get(0);
                    if (cookie.contains(JWT_APP_COOKIE.toString())) {
                        String jwt = getJwt(cookie);
                        if (isJwtValid(jwt)) {
                            sessionsService.storeForAuthentication(session.getId(), getLogin(jwt), getPwd(jwt));
                            LOG.debug("Session [{}] might be authenticated", session.getId());
                        }
                    }
                } catch (Exception e) {
                    //not able for authentication
                    LOG.debug("Should be exception related to jwt validation, check this out: \n", e);
                }
                super.afterConnectionEstablished(session);
            }

            private String getJwt(String cookie) {
                int start = cookie.lastIndexOf(JWT_APP_COOKIE.toString());
                int end = cookie.lastIndexOf("; ") <= start ? cookie.length() : cookie.indexOf("; ", start);
                return cookie.substring(start, end).split("=", 2)[1];
            }

            private boolean isJwtValid(String jwt) throws HmacException, ExpiredAuthenticationException, ParseException {
                String iss = HmacSigner.getJwtClaim(jwt, JWT_CLAIM_LOGIN.toString());
                String privateKey = checkService.getPrivateSecret(iss);
                return HmacSigner.getJwtClaim(jwt, CSRF_CLAIM_HEADER.toString()) != null && HmacSigner.verifyJWT(jwt, privateKey) && !HmacSigner.isJwtExpired(jwt);
            }

            private String getLogin(String jwt) throws HmacException {
                return HmacSigner.getJwtClaim(jwt, JWT_CLAIM_LOGIN.toString());
            }

            private String getPwd(String jwt) throws HmacException {
                return HmacSigner.getJwtClaim(jwt, CSRF_CLAIM_HEADER.toString());
            }
        };
    }
}
