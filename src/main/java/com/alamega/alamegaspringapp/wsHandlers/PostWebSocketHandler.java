package com.alamega.alamegaspringapp.wsHandlers;

import com.alamega.alamegaspringapp.SystemData;
import com.alamega.alamegaspringapp.info.Info;
import com.alamega.alamegaspringapp.info.InfoRepository;
import org.json.JSONObject;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

@Component
public class PostWebSocketHandler extends TextWebSocketHandler {
    private final HashMap<WebSocketSession, String> macSessions = new HashMap<>();
    final InfoRepository infoRepository;
    final SoloInfoWebSocketHandler soloInfoWebSocketHandler;
    final InfoWebSocketHandler infoWebSocketHandler;
    final SystemData systemData;

    final String password;

    public PostWebSocketHandler(InfoRepository infoRepository, SoloInfoWebSocketHandler soloInfoWebSocketHandler, InfoWebSocketHandler infoWebSocketHandler, SystemData systemData) {
        this.infoRepository = infoRepository;
        this.soloInfoWebSocketHandler = soloInfoWebSocketHandler;
        this.infoWebSocketHandler = infoWebSocketHandler;
        this.systemData = systemData;
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("config.ini"));
        } catch (IOException ignored) {}
        this.password  = props.getProperty("PASSWORD", "1111");
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        String mac = session.getHandshakeHeaders().getFirst("mac");
        macSessions.put(session, mac);
        Info info = infoRepository.findByMac(mac);
        if (info != null) {
            info.setOnline(true);
            infoRepository.save(info);
        }
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        JSONObject json = new JSONObject(message.getPayload());
        if (json.getString("password").equals(this.password) && macSessions.containsKey(session)) {
            systemData.addServerInfo(json);
            infoWebSocketHandler.sendOneInfo(json.toString());
            soloInfoWebSocketHandler.sendOneInfoSolo(json.toString());
            systemData.addJsonByMac(json.getString("mac"), json);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session,@NonNull  CloseStatus status) {
        String mac = macSessions.get(session);

        Info info = infoRepository.findByMac(mac);
        if (info != null) {
            info.setOnline(false);
            infoRepository.save(info);
        }

        JSONObject json = systemData.All.get(mac).get(systemData.All.get(mac).size() - 1);
        systemData.addServerInfo(json);
        systemData.addJsonByMac(mac, json);
        infoWebSocketHandler.sendOneInfo(json.toString());
        soloInfoWebSocketHandler.sendOneInfoSolo(json.toString());
        macSessions.remove(session);
    }
}