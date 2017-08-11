package ru.ilonich.igps.config.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.*;
import ru.ilonich.igps.config.data.misc.JsonMapper;
import ru.ilonich.igps.service.SecuredRequestCheckService;
import ru.ilonich.igps.service.cacheonly.WebSocketSessionsService;

import java.util.List;

/**
*  Для чего это?
*  1) Добавлять комментарии к посту который читает юзер
*  2) Получать и отправлять сообщения в диалоге, уведомлять о прочтении
*  3) Отслеживать в диалоге друг друга, "печатает", "онлайн" или что-то вроде.
*
 * What if you want to send messages to connected clients from any part of the application?
 * Any application component can send messages to the "brokerChannel". The easiest way to do that
 * is to have a SimpMessagingTemplate injected, and use it to send messages.
 * Typically it should be easy to have it injected by type, for example:
 * @Autowired
 * public GreetingController(SimpMessagingTemplate template) {
 * this.template = template;
 * }
 *
 * 	public void afterTradeExecuted(Trade trade) {
 * 	this.messagingTemplate.convertAndSendToUser(
 * 	trade.getUserName(), "/queue/position-updates", trade.getResult());
 * 	}
* */

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Autowired
    @Qualifier("clientOutboundChannel")
    private MessageChannel clientOutboundChannel;

    @Autowired
    private WebSocketSessionsService webSocketSessionsService;

    @Autowired
    private SecuredRequestCheckService checkService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/websocket").setAllowedOrigins("http://localhost:4200").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/in");
        registry.enableSimpleBroker("/out");

        /*
        registry.enableStompBrokerRelay("/topic", "/queue");

        Check the STOMP documentation for your message broker of choice (e.g. RabbitMQ, ActiveMQ, etc.),
        install the broker, and run it with STOMP support enabled. Then enable the STOMP broker relay in the Spring
        configuration instead of the simple broker.

        A STOMP broker relay maintains a single "system" TCP connection to the broker.
        This connection is used for messages originating from the server-side application only,
        not for receiving messages. You can configure the STOMP credentials for this connection,
        i.e. the STOMP frame login and passcode headers.

        This is exposed in both the XML namespace and the Java config as the systemLogin/systemPasscode properties
        with default values guest/guest.

        The STOMP broker relay always sets the login and passcode headers on every CONNECT frame that it forwards
        to the broker on behalf of clients. Therefore WebSocket clients need not set those headers; they will be ignored.
        As the following section explains, instead WebSocket clients should rely on HTTP authentication to protect the
        WebSocket endpoint and establish the client identity.

        The STOMP broker relay also sends and receives heartbeats to and from the message broker over the "system" TCP connection.
        You can configure the intervals for sending and receiving heartbeats (10 seconds each by default). If connectivity to
        the broker is lost, the broker relay will continue to try to reconnect, every 5 seconds, until it succeeds.

        A Spring bean can implement ApplicationListener<BrokerAvailabilityEvent> in order to receive notifications when the "system"
        connection to the broker is lost and re-established. For example a Stock Quote service broadcasting stock quotes can stop trying
        to send messages when there is no active "system" connection.
        */
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        /*
        Instead of using cookies they may prefer to authenticate with headers at the STOMP messaging protocol level
        There are 2 simple steps to doing that:
        1) Use the STOMP client to pass authentication header(s) at connect time.
        2) Process the authentication header(s) with a ChannelInterceptor.
        Note that an interceptor only needs to authenticate and set the user header on the CONNECT Message.
        Spring will note and save the authenticated user and associate it with subsequent STOMP messages on the same session:

        registration.setInterceptors(new ChannelInterceptorAdapter() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {

                StompHeaderAccessor accessor =
                    MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    Principal user = ... ; // access authentication header(s)
                    accessor.setUser(user);
                }

                return message;
            }
        });
         */
        registration.setInterceptors(new AuthConnectionInterceptor(clientOutboundChannel, webSocketSessionsService));
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setSendTimeLimit(15 * 1000).setSendBufferSizeLimit(512 * 1024);
        registry.addDecoratorFactory(new CheckJwtCookiesWSHDecoratorFactory(checkService, webSocketSessionsService));
/*       * Add a factory that to decorate the handler used to process WebSocket
         * messages. This may be useful for some advanced use cases, for example
         * to allow Spring Security to forcibly close the WebSocket session when
         * the corresponding HTTP session expires.
         * registry.addDecoratorFactory()*/
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
