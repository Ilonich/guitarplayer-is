package ru.ilonich.igps.config.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.WebSocketSession;
import ru.ilonich.igps.exception.ExpiredAuthenticationException;
import ru.ilonich.igps.exception.HmacException;
import ru.ilonich.igps.model.SocketPrincipal;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.service.SecuredRequestCheckService;
import ru.ilonich.igps.service.UserService;
import ru.ilonich.igps.service.cacheonly.WebSocketSessionsService;
import ru.ilonich.igps.utils.HmacSigner;

import java.security.Principal;
import java.text.ParseException;
import java.util.List;

import static ru.ilonich.igps.config.security.HmacSecurityFilter.CLAIM_WITH_CURRENT_ENCODING;
import static ru.ilonich.igps.config.security.misc.SecurityConstants.*;


public class AuthenticateConnectionsInterceptor extends ChannelInterceptorAdapter {
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private final static String GUEST = "guest";

    private MessageChannel clientOutboundChannel;
    private WebSocketSessionsService sessionsService;
    private SecuredRequestCheckService checkService;
    private UserService userService;

    public AuthenticateConnectionsInterceptor(MessageChannel clientOutboundChannel, WebSocketSessionsService sessionsService,
                                              SecuredRequestCheckService checkService, UserService userService) {
        this.clientOutboundChannel = clientOutboundChannel;
        this.sessionsService = sessionsService;
        this.checkService = checkService;
        this.userService = userService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor incMessage = StompHeaderAccessor.wrap(message);
        String sessionId = incMessage.getSessionId();
        WebSocketSession session = sessionsService.getSessionById(sessionId);
        if (StompCommand.CONNECT.equals(incMessage.getCommand())) {
            LOG.info("CONNECT session [{}]", sessionId);
            Principal principal = null;
            if (GUEST.equalsIgnoreCase(incMessage.getLogin())) {
                principal = new SocketPrincipal(session.getRemoteAddress().getHostString());
            } else {
                try {
                    principal = new SocketPrincipal(getUser(incMessage));
                } catch (ParseException | ExpiredAuthenticationException | HmacException e) {
                    sendError(sessionId, e.getMessage());
                }
            }
            incMessage.setUser(principal);
        } else if (StompCommand.DISCONNECT.equals(incMessage.getCommand())) {
        }
        return super.preSend(message, channel);
    }

    private User getUser(StompHeaderAccessor incMessage) throws ParseException, ExpiredAuthenticationException, HmacException {
        String sessionId = incMessage.getSessionId();
        String messageCsrfLogin = incMessage.getLogin();
        String messageEncodedCsrfPwd = incMessage.getPasscode();
        WebSocketSession session = sessionsService.getSessionById(sessionId);
        String cookieString = getCookieStringFromSessionHandshakeHeaders(session);
        String jwtValue = parseJwtStringValue(cookieString);
        String login = validateJwtAndGetIss(jwtValue);
        String jwtCsrf = getCsrf(jwtValue);
        String serverEncodedCsrf = HmacSigner.encodeMac(checkService.getPublicSecret(login), jwtCsrf, CLAIM_WITH_CURRENT_ENCODING.get(ENCODING_CLAIM_PROPERTY.toString()));
        if (messageCsrfLogin.equals(jwtCsrf) && messageEncodedCsrfPwd.equals(serverEncodedCsrf)) {
            return userService.getByEmail(login);
        } else {
            throw new HmacException("No matches");
        }
    }

    private String getCookieStringFromSessionHandshakeHeaders(WebSocketSession socketSession) {
        HttpHeaders httpHeaders = socketSession.getHandshakeHeaders();
        List<String> cookie = httpHeaders.get(HttpHeaders.COOKIE);
        if (cookie == null) return "";
        StringBuilder builder = new StringBuilder();
        cookie.forEach((s) -> builder.append("; ").append(s));
        return builder.toString();
    }

    private String parseJwtStringValue(String cookie) throws ParseException {
        int start = cookie.lastIndexOf(JWT_APP_COOKIE.toString());
        int end = cookie.lastIndexOf("; ") <= start ? cookie.length() : cookie.indexOf("; ", start);
        if (start < 0) throw new ParseException("No jwt cookie found", start);
        return cookie.substring(start, end).split("=", 2)[1];
    }

    private String validateJwtAndGetIss(String jwt) throws HmacException, ExpiredAuthenticationException, ParseException {
        String iss = HmacSigner.getJwtClaim(jwt, JWT_CLAIM_LOGIN.toString());
        String privateKey = checkService.getPrivateSecret(iss);
        if (!HmacSigner.verifyJWT(jwt, privateKey)) throw new HmacException("Jwt is invalid");
        if (HmacSigner.isJwtExpired(jwt)) throw new HmacException("Jwt is expired");
        return iss;
    }

    private String getCsrf(String jwt) throws HmacException {
        return HmacSigner.getJwtClaim(jwt, CSRF_CLAIM_HEADER.toString());
    }

    private void sendError(String sessionId, String message) {
        StompHeaderAccessor errorAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
        errorAccessor.setNativeHeader("security", "true");
        errorAccessor.setMessage(message);
        errorAccessor.setSessionId(sessionId);
        this.clientOutboundChannel.send(MessageBuilder.createMessage(errorAccessor.getMessage().getBytes(), errorAccessor.getMessageHeaders()));
    }
}
