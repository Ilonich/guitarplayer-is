package ru.ilonich.igps.comtroller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import ru.ilonich.igps.config.socket.WebSocketConfig;

/*
* By default the return value from an @SubscribeMapping method is sent as a message directly back
 * to the connected client and does not pass through the broker. This is useful for implementing
 * request-reply message interactions; for example, to fetch application data when the application
 * UI is being initialized. Or alternatively an @SubscribeMapping method can be annotated with @SendTo in which case
 * the resulting message is sent to the "brokerChannel" using the specified target destination.
 *
 Message method argument to get access to the complete message being processed.
 @Payload-annotated argument for access to the payload of a message, converted with a org.springframework.messaging.converter.MessageConverter. The presence of the annotation is not required since it is assumed by default. Payload method arguments annotated with validation annotations (like @Validated) will be subject to JSR-303 validation.
 @Header-annotated arguments for access to a specific header value along with type conversion using an org.springframework.core.convert.converter.Converter if necessary.
 @Headers-annotated method argument that must also be assignable to java.util.Map for access to all headers in the message.
 MessageHeaders method argument for getting access to a map of all headers.
 MessageHeaderAccessor, SimpMessageHeaderAccessor, or StompHeaderAccessor for access to headers via typed accessor methods.
 @DestinationVariable-annotated arguments for access to template variables extracted from the message destination. Values will be converted to the declared method argument type as necessary.
 java.security.Principal method arguments reflecting the user logged in at the time of the WebSocket HTTP handshake.
*
* */

@ConditionalOnBean(WebSocketConfig.class)
@Controller
public class DialogController {

    @MessageMapping("/websocket/test")
    public String test() {
        return "Something";
    }

}
