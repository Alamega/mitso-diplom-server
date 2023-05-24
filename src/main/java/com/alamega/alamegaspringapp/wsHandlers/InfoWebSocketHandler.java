package com.alamega.alamegaspringapp.wsHandlers;

import com.alamega.alamegaspringapp.SystemData;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Component
public class InfoWebSocketHandler extends TextWebSocketHandler {
    private final List<WebSocketSession> sessions = new ArrayList<>();
    final SystemData systemData;

    public InfoWebSocketHandler(SystemData systemData) {
        this.systemData = systemData;
    }

    public void sendOneInfo(String message) {
        sessions.forEach(session -> {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException ignored) { }
        });
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        systemData.All.forEach((s, jsonObject) -> {
            try {
                session.sendMessage(new TextMessage(jsonObject.get(jsonObject.size() - 1).toString()));
            } catch (IOException ignored) { }
        });
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session,@NonNull  TextMessage message) {}

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session,@NonNull  CloseStatus status) {
        sessions.remove(session);
    }
}