package ru.ilonich.igps.config.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import ru.ilonich.igps.model.User;

import java.security.Principal;


public class AuthenticateConnectionsInterceptor extends ChannelInterceptorAdapter {
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private StompWebSocketSessionsContextManager contextManager;

    AuthenticateConnectionsInterceptor(StompWebSocketSessionsContextManager contextManager) {
        this.contextManager = contextManager;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor incMessage = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(incMessage.getCommand())) {
            User user = contextManager.authenticate(incMessage);
            if (user != null) {
                incMessage.setUser(new PrincipalImpl(user));
            }
        } else if (StompCommand.DISCONNECT.equals(incMessage.getCommand())) {
            User user = contextManager.logout(incMessage);
            if (user != null && !contextManager.isUserOnline(user)) {
                LOG.trace("User id[{}] disconnected fully", user.getId());
            }
        }
        return message;
    }

    private static class PrincipalImpl implements Principal {
        private String nameAsId;

        PrincipalImpl(User user) {
            this.nameAsId = user.getId().toString();
        }

        @Override
        public String getName() {
            return nameAsId;
        }

        @Override
        public int hashCode() {
            return nameAsId.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj){
                return true;
            } else if (!(obj instanceof PrincipalImpl)) {
                return false;
            }
            PrincipalImpl another = (PrincipalImpl) obj;
            return another.getName().equals(this.nameAsId);
        }

        @Override
        public String toString() {
            return "Principal[" +
                    "name='" + nameAsId + '\'' +
                    ']';
        }
    }
}
