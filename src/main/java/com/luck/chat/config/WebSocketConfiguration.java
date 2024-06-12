package com.luck.chat.config;

import com.luck.chat.Interceptor.WebSocketInterceptor;
import com.luck.chat.websocket.WebSocketSingleServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@EnableWebSocket
@Configuration
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Autowired
    private WebSocketInterceptor webSocketInterceptor;

    @Autowired
    private WebSocketSingleServer webSocketSingleServer;

    /**
     * 注册websocket拦截器
     * @param registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketSingleServer,"/ws")
                .addInterceptors(webSocketInterceptor)
                .setAllowedOrigins("*");
    }
}
