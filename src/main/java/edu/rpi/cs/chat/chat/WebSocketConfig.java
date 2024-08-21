package edu.rpi.cs.chat.chat;

import edu.rpi.cs.chat.chat.data.repository.GroupRepository;
import edu.rpi.cs.chat.chat.data.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * the configuration for the web socket
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    /**
     * Default constructor
     */
    public WebSocketConfig() {
    }

    /**
     * the msg repo
     */
    @Autowired
    private MessageRepository messageRepo;

    @Autowired
    private GroupRepository groupRepo;

    /**
     * registers the ws handler
     *
     * @param registry the registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MessageSocketHandler(messageRepo, groupRepo), "/msg/{username}")
                .addInterceptors(getInter())
                .setAllowedOrigins("*");
    }

    /**
     * the intercepter to get the path the person connected to the ws used
     *
     * @return a handshake interceptor
     */
    private HandshakeInterceptor getInter() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest serverHttpRequest,
                                           ServerHttpResponse serverHttpResponse,
                                           WebSocketHandler webSocketHandler,
                                           Map<String, Object> map) {

                String path = serverHttpRequest.getURI().getPath();

                // gets the username connected
                int index = path.indexOf("/msg");
                String username = path.substring(index + 5);
                map.put("username", username);
                return true;
            }

            /**
             * ignored
             * @param request the current request
             * @param response the current response
             * @param wsHandler the target WebSocket handler
             * @param exception an exception raised during the handshake, or {@code null} if none
             */
            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
            }
        };
    }
}
