package uno.cod.platform.server.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.user.UserShowDto;
import uno.cod.platform.server.core.service.UserService;
import uno.cod.platform.server.core.service.WebSocketService;

/**
 * Created by vbalan on 2/23/2016.
 */
@Configuration
@EnableWebSocket
@ComponentScan({"uno.cod.platform.server.core.service"})
public class WebSocketConfig implements WebSocketConfigurer{
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private UserService userService;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketHandler(), "/ws").setAllowedOrigins("*").withSockJS();
    }

    private class SocketHandler extends TextWebSocketHandler{
        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            UserShowDto user = userService.findByUsername(session.getPrincipal().getName());
            webSocketService.addSession(user.getId(), session);
        }

    }
}
