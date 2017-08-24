package ru.ilonich.igps.config.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

public class CheckUserSubscribePermissionInterceptor extends ChannelInterceptorAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(CheckUserSubscribePermissionInterceptor.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor incMessage = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(incMessage.getCommand())) {
            LOG.debug("SUBSCRIBE destination = {}", incMessage.getNativeHeader("destination"));
        }
        return message;
    }
}
