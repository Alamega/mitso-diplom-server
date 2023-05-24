package com.alamega.alamegaspringapp.wsHandlers;

import com.alamega.alamegaspringapp.SystemData;
import org.json.JSONObject;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class SoloInfoWebSocketHandler extends TextWebSocketHandler {
    private final List<WebSocketSession> watchers = new ArrayList<>();
    private final HashMap<String, List<WebSocketSession>> macWatchers = new HashMap<>();
    final SystemData systemData;

    public SoloInfoWebSocketHandler(SystemData systemData) {
        this.systemData = systemData;
    }
    public void sendOneInfoSolo(String message) {
        JSONObject json = new JSONObject(message);
        if (json.has("mac") && macWatchers.containsKey(json.getString("mac"))) {
            macWatchers.get(json.getString("mac")).forEach(session -> {
                try {
                    session.sendMessage(new TextMessage(json.toString()));
                } catch (IOException ignored) {
                }
            });
        }
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        watchers.add(session);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session,@NonNull  TextMessage message) {
        if (!message.getPayload().equals("ping")) {
            if (!macWatchers.containsKey(message.getPayload())) {
                macWatchers.put(message.getPayload(), new ArrayList<>());
            }
            macWatchers.get(message.getPayload()).add(session);
            if (systemData.All.containsKey(message.getPayload())) {
                try {
                    session.sendMessage(new TextMessage(systemData.All.get(message.getPayload()).get(systemData.All.get(message.getPayload()).size() - 1).toString()));
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session,@NonNull  CloseStatus status) {
        watchers.remove(session);
        macWatchers.forEach((mac, webSocketSessions) -> {
            macWatchers.get(mac).remove(session);
        });
    }
}