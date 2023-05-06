package com.alamega.alamegaspringapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import static com.alamega.alamegaspringapp.wsHandlers.InfoWebSocketHandler.webSocket;
import static com.alamega.alamegaspringapp.wsHandlers.SoloInfoWebSocketHandler.soloWebSocket;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocket, "/webSocketInfo").setAllowedOrigins("*");
        registry.addHandler(soloWebSocket, "/soloWebSocketInfo").setAllowedOrigins("*");
    }
}
