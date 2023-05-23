package com.alamega.alamegaspringapp.config;

import com.alamega.alamegaspringapp.wsHandlers.InfoWebSocketHandler;
import com.alamega.alamegaspringapp.wsHandlers.PostWebSocketHandler;
import com.alamega.alamegaspringapp.wsHandlers.SoloInfoWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    final PostWebSocketHandler postWebSocketHandler;
    final InfoWebSocketHandler infoWebSocketHandler;
    final SoloInfoWebSocketHandler soloInfoWebSocketHandler;

    public WebSocketConfig(PostWebSocketHandler postWebSocketHandler, InfoWebSocketHandler infoWebSocketHandler, SoloInfoWebSocketHandler soloInfoWebSocketHandler) {
        this.postWebSocketHandler = postWebSocketHandler;
        this.infoWebSocketHandler = infoWebSocketHandler;
        this.soloInfoWebSocketHandler = soloInfoWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(infoWebSocketHandler, "/webSocketInfo").setAllowedOrigins("*");
        registry.addHandler(soloInfoWebSocketHandler, "/soloWebSocketInfo").setAllowedOrigins("*");
        registry.addHandler(postWebSocketHandler, "/post").setAllowedOrigins("*");
    }
}
