package ru.ilonich.igps.config.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import ru.ilonich.igps.service.cacheonly.WebSocketSessionsService;

public class RegisterSessionWebSocketHanlerDecoratorFactory implements WebSocketHandlerDecoratorFactory {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private WebSocketSessionsService sessionsService;

    RegisterSessionWebSocketHanlerDecoratorFactory(WebSocketSessionsService sessionsService) {
        this.sessionsService = sessionsService;
    }

    @Override
    public WebSocketHandler decorate(WebSocketHandler handler) {
        return new UserSessionWebSocketHandler(handler);
    }

    private final class UserSessionWebSocketHandler extends WebSocketHandlerDecorator {
        public UserSessionWebSocketHandler(WebSocketHandler delegate) {
            super(delegate);
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            //https://jira.spring.io/browse/SPR-12812
            //https://stackoverflow.com/questions/36692582/some-spring-websocket-sessions-never-disconnect
            LOG.trace("#afterConnectionEstablished [{}]", session.getId());
            super.afterConnectionEstablished(session);
            sessionsService.registerSession(session);
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            LOG.trace("#handleTransportError [{}] {}", session.getId(), exception.getMessage());
            super.handleTransportError(session, exception);
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
            LOG.trace("#afterConnectionClosed [{}] {}", session.getId(), closeStatus.toString());
            super.afterConnectionClosed(session, closeStatus);
        }
    }

}
