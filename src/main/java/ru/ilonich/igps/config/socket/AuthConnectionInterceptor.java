package ru.ilonich.igps.config.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageBuilder;
import ru.ilonich.igps.model.SocketPrincipal;
import ru.ilonich.igps.service.cacheonly.WebSocketSessionsService;


public class AuthConnectionInterceptor extends ChannelInterceptorAdapter {
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private final static String GUEST = "guest";
    private final static String INVALID_TOKEN = "Jwt token was expired or invalid";
    private final static String INVALID_LOGIN = "Invalid login";

    private MessageChannel clientOutboundChannel;
    private WebSocketSessionsService sessionsService;

    public AuthConnectionInterceptor(MessageChannel clientOutboundChannel, WebSocketSessionsService sessionsService) {
        this.clientOutboundChannel = clientOutboundChannel;
        this.sessionsService = sessionsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor incMessage = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(incMessage.getCommand())) {
            if (!GUEST.equalsIgnoreCase(incMessage.getLogin())) {
                processAuthentication(incMessage);
            }
        } else if (StompCommand.CONNECTED.equals(incMessage.getCommand())) {
        } else if (StompCommand.DISCONNECT.equals(incMessage.getCommand())) {
            SocketPrincipal principal = (SocketPrincipal) incMessage.getUser();
            if (principal != null) {
                sessionsService.removeOnlineUserId(principal.getName());
            }
        }
        return message;
    }

    private void processAuthentication(StompHeaderAccessor incMessage) {
        SocketPrincipal principal = sessionsService.getPrincipalCandidate(incMessage.getSessionId());
        if (principal == null) {
            sendError(incMessage.getSessionId(), INVALID_TOKEN);
        } else {
            if (!isPrincipalMatches(principal, incMessage.getLogin(), incMessage.getPasscode())) {
                sendError(incMessage.getSessionId(), INVALID_LOGIN);
            } else {
                incMessage.setUser(principal);
                LOG.debug("Session [{}] authenticated as {}", incMessage.getId(), principal.toString());
                sessionsService.addOnlineUserId(principal.getName());
            }
        }
    }

    private boolean isPrincipalMatches(SocketPrincipal principal, String login, String passcode) {
        return principal.getEmail().equalsIgnoreCase(login) && principal.getCsrfAsPwd().equals(passcode);
    }

    private void sendError(String sessionId, String message) {
        StompHeaderAccessor errorAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
        errorAccessor.setNativeHeader("security", "true");
        errorAccessor.setMessage(message);
        errorAccessor.setSessionId(sessionId);
        this.clientOutboundChannel.send(MessageBuilder.createMessage(errorAccessor.getMessage().getBytes(), errorAccessor.getMessageHeaders()));
    }
}
