package ru.ilonich.igps.config.socket;

import org.springframework.http.HttpHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.WebSocketSession;
import ru.ilonich.igps.exception.ExpiredAuthenticationException;
import ru.ilonich.igps.exception.HmacException;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.service.SecuredRequestCheckService;
import ru.ilonich.igps.service.UserService;
import ru.ilonich.igps.utils.HmacSigner;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

import static ru.ilonich.igps.config.security.HmacSecurityFilter.CLAIM_WITH_CURRENT_ENCODING;
import static ru.ilonich.igps.config.security.misc.SecurityConstants.*;

public class StompWebSocketSessionsContextManager {

    private final static String GUEST = "guest";
    private final static String GUEST_MARK = "@";

    private MessageChannel clientOutboundChannel;
    private SecuredRequestCheckService checkService;
    private UserService userService;

    public StompWebSocketSessionsContextManager(MessageChannel clientOutboundChannel, SecuredRequestCheckService checkService,
                                                UserService userService) {
        this.clientOutboundChannel = clientOutboundChannel;
        this.checkService = checkService;
        this.userService = userService;
    }

    //см. StompSubProtocolHandler.sendToClient (finally блок), CloseStatus = 1002
    public void sendSecurityError(String sessionId, String message) {
        StompHeaderAccessor errorAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
        errorAccessor.setNativeHeader("security", "true");
        errorAccessor.setMessage(message);
        errorAccessor.setSessionId(sessionId);
        this.clientOutboundChannel.send(MessageBuilder.createMessage(errorAccessor.getMessage().getBytes(), errorAccessor.getMessageHeaders()));
    }

    public User getUser(String sessionId) {
        String username = WebSocketSessionsContextHolder.getUsernameAssociatedWithSession(sessionId);
        return recognizeUsernameIsValidUserId(username);
    }

    public User getUser(StompHeaderAccessor incMessage) {
        return getUser(incMessage.getSessionId());
    }

    public boolean isUserOnline(User user) {
        return isUserOnline(user.getId());
    }

    public boolean isUserOnline(Integer userId) {
        return WebSocketSessionsContextHolder.isRegisteredUserHasAnySession(userId);
    }

    public Set<Integer> getOnlineUsersIds() {
        return WebSocketSessionsContextHolder.getUserIdsThatHasAssociationWithAnySession();
    }

    User authenticate(StompHeaderAccessor incMessage) {
        if (!StompCommand.CONNECT.equals(incMessage.getCommand())) {
            throw new IllegalArgumentException("Authentication can only be performed for CONNECT stomp messages");
        }

        String sessionId = incMessage.getSessionId();
        WebSocketSession session = WebSocketSessionsContextHolder.getSessionById(sessionId);
        String messageLogin = incMessage.getLogin();
        if (messageLogin.equalsIgnoreCase(GUEST)) {
            WebSocketSessionsContextHolder.associateSessionWithUsername(sessionId, GUEST_MARK + session.getRemoteAddress().getHostString());
            return null; //anonymous
        }

        String messageEncodedCsrfPwd = incMessage.getPasscode();
        try {
            String cookieString = getCookieStringFromSessionHandshakeHeaders(session);
            String jwtValue = parseJwtStringValue(cookieString);
            Integer userId = validateJwtAndGetUserId(jwtValue);
            String jwtCsrf = getCsrf(jwtValue);
            String serverEncodedCsrf = HmacSigner.encodeMac(checkService.getPublicSecret(userId), jwtCsrf, CLAIM_WITH_CURRENT_ENCODING.get(ENCODING_CLAIM_PROPERTY.toString()));
            if (messageLogin.equals(jwtCsrf) && messageEncodedCsrfPwd.equals(serverEncodedCsrf)) {
                User user = userService.getById(userId);
                WebSocketSessionsContextHolder.associateSessionWithUsername(sessionId, user.getId().toString());
                WebSocketSessionsContextHolder.addUserId(user.getId());
                return user;
            } else {
                throw new HmacException("Login or passcode are incorrect");
            }
        } catch (HmacException | ParseException | ExpiredAuthenticationException e) {
            sendSecurityError(sessionId, e.getMessage());
            return null;
        }
    }

    User logout(StompHeaderAccessor incMessage) {
        if (!StompCommand.DISCONNECT.equals(incMessage.getCommand())) {
            throw new IllegalArgumentException("Logout can only be performed for DISCONNECT stomp messages");
        }
        String sessionId = incMessage.getSessionId();
        String username = WebSocketSessionsContextHolder.removeSessionUsernameAssociation(sessionId);
        User user = recognizeUsernameIsValidUserId(username);
        if (user != null && !isIdUsernameHasSession(user)) {
            WebSocketSessionsContextHolder.removeUserId(user.getId());
        }
        return user;
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

    private Integer validateJwtAndGetUserId(String jwt) throws HmacException, ExpiredAuthenticationException, ParseException {
        Integer userId = Integer.valueOf(HmacSigner.getJwtIss(jwt));
        String privateKey = checkService.getPrivateSecret(userId);
        if (!HmacSigner.verifyJWT(jwt, privateKey)) throw new HmacException("Jwt is invalid");
        if (HmacSigner.isJwtExpired(jwt)) throw new HmacException("Jwt is expired");
        return userId;
    }

    private String getCsrf(String jwt) throws HmacException {
        return HmacSigner.getJwtClaim(jwt, CSRF_CLAIM_HEADER.toString());
    }

    private User recognizeUsernameIsValidUserId(String username) {
        if (username == null || username.startsWith(GUEST_MARK)) {
            return null;
        } else {
            return userService.getById(Integer.valueOf(username));
        }
    }

    private boolean isIdUsernameHasSession(Integer userId) {
        return WebSocketSessionsContextHolder.isUsernameAssociatedWithAnySession(userId.toString());
    }

    private boolean isIdUsernameHasSession(User user) {
        return isIdUsernameHasSession(user.getId());
    }
}
