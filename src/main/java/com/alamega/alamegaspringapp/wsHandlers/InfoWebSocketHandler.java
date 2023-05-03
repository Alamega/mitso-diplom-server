package com.alamega.alamegaspringapp.wsHandlers;

import com.alamega.alamegaspringapp.SystemData;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InfoWebSocketHandler extends TextWebSocketHandler {
    private final List<WebSocketSession> webSocketSessions = new ArrayList<>();

    public static InfoWebSocketHandler webSocket = new InfoWebSocketHandler();

    public void sendOneInfo(String message) {
        webSocketSessions.forEach(session -> {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException ignored) { }
        });
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        //При подключении нового челобаса отправляем все данные
        SystemData.All.forEach((s, jsonObject) -> {
            try {
                session.sendMessage(new TextMessage(jsonObject.toString()));
            } catch (IOException ignored) { }
        });
        //Добавляем чела в список сессий
        webSocketSessions.add(session);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session,@NonNull  TextMessage message) {}

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session,@NonNull  CloseStatus status) {
        webSocketSessions.remove(session);
    }
}