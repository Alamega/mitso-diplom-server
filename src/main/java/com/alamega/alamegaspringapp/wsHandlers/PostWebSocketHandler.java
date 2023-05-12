package com.alamega.alamegaspringapp.wsHandlers;

import com.alamega.alamegaspringapp.SystemData;
import com.alamega.alamegaspringapp.info.Info;
import com.alamega.alamegaspringapp.info.InfoRepository;
import com.alamega.alamegaspringapp.user.UserRepository;
import org.json.JSONObject;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.alamega.alamegaspringapp.wsHandlers.InfoWebSocketHandler.webSocket;
import static com.alamega.alamegaspringapp.wsHandlers.SoloInfoWebSocketHandler.soloWebSocket;

public class PostWebSocketHandler extends TextWebSocketHandler {
    private final List<WebSocketSession> sessions = new ArrayList<>();

    public static PostWebSocketHandler postWebSocket = new PostWebSocketHandler();

    private static InfoRepository infoRepository;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session,@NonNull  TextMessage message) {
        JSONObject json = new JSONObject(message.getPayload());
        webSocket.sendOneInfo(message.getPayload());
        soloWebSocket.sendOneInfoSolo(message.getPayload());
        SystemData.All.put(json.getString("mac"), json);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session,@NonNull  CloseStatus status) {
        sessions.remove(session);
    }
}