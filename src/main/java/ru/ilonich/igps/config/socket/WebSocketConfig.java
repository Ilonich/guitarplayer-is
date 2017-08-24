package ru.ilonich.igps.config.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.*;
import ru.ilonich.igps.config.data.misc.JsonMapper;
import ru.ilonich.igps.service.SecuredRequestCheckService;
import ru.ilonich.igps.service.UserService;

import java.util.List;

//TODO удалить копипасту документации Spring
/**
 * https://github.com/sockjs/sockjs-node#authorisation
 * https://stackoverflow.com/questions/25486889/websocket-stomp-over-sockjs-http-custom-headers
 * https://stackoverflow.com/questions/43475884/spring-security-token-based-authentication-for-sockjs-stomp-web-socket
 * Мой велосипед: после handshake socket-сессию в контекст, при получении сообщения CONNECT брать из сессии handshake заголовки,
 * парсить jwt токен из cookie и валидировать c приватным ключом, а также проверять stomp login & passcode заголовки
 * в которые клиент устанавливает raw csrf и csrf закодированный hmac публичным ключом конкретного issuer'a из jwt
 * После проверки сохраняется в контекст связь юзера и сессии, см. StompWebSocketSessionsContextManager.authenticate(message)
 * StompWebSocketSessionsContextManager.getUser(session) мой аналог SecurityContextHolder.getContext().getAuthentication();
 *
 */

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer { //WebSocketMessageBrokerConfigurationSupport

    @Autowired
    @Qualifier("clientOutboundChannel")
    private MessageChannel clientOutboundChannel;

    @Autowired
    private SecuredRequestCheckService checkService;

    @Autowired
    private UserService userService;

    @Bean
    public StompWebSocketSessionsContextManager webSocketSessionsContextManager(){
        return new StompWebSocketSessionsContextManager(clientOutboundChannel, checkService, userService);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/websocket").setAllowedOrigins("http://localhost:4200").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/queue");
        registry.enableSimpleBroker("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.setInterceptors(new AuthenticateConnectionsInterceptor(webSocketSessionsContextManager()));
        registration.setInterceptors(new CheckUserSubscribePermissionInterceptor());
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setSendTimeLimit(15 * 1000).setSendBufferSizeLimit(512 * 1024);
        registry.addDecoratorFactory(new RegisterSessionWebSocketHanlerDecoratorFactory());
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(2);
        registration.taskExecutor().maxPoolSize(4);
        registration.taskExecutor().queueCapacity(100);
        registration.taskExecutor().keepAliveSeconds(5);
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(JsonMapper.getMapper());
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);
        return false;
    }
}
