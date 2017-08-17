package ru.ilonich.igps.config.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

public class RegisterSessionWebSocketHanlerDecoratorFactory implements WebSocketHandlerDecoratorFactory {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    RegisterSessionWebSocketHanlerDecoratorFactory() {
    }

    @Override
    public WebSocketHandler decorate(WebSocketHandler handler) {
        return new RegisterSessionWebSocketHandler(handler);
    }

    private final class RegisterSessionWebSocketHandler extends WebSocketHandlerDecorator {
        public RegisterSessionWebSocketHandler(WebSocketHandler delegate) {
            super(delegate);
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            //https://jira.spring.io/browse/SPR-12812?focusedCommentId=135465&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-135465
            //https://stackoverflow.com/questions/36692582/some-spring-websocket-sessions-never-disconnect
            LOG.trace("#afterConnectionEstablished [{}]", session.getId());
            super.afterConnectionEstablished(session);
            WebSocketSessionsContextHolder.registerSession(session);
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            LOG.trace("#handleTransportError [{}] {}", session.getId(), exception.getMessage());
            super.handleTransportError(session, exception);
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
            String sessionId = session.getId();
            LOG.trace("#afterConnectionClosed [{}] {}", sessionId, closeStatus.toString());
            super.afterConnectionClosed(session, closeStatus);
            WebSocketSessionsContextHolder.deregisterSession(sessionId);
        }
    }

}
