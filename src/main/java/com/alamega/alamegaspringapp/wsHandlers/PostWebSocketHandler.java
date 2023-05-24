package com.alamega.alamegaspringapp.wsHandlers;

import com.alamega.alamegaspringapp.SystemData;
import com.alamega.alamegaspringapp.info.Info;
import com.alamega.alamegaspringapp.info.InfoRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class PostWebSocketHandler extends TextWebSocketHandler {
    private final HashMap<WebSocketSession, String> macSessions = new HashMap<>();
    final InfoRepository infoRepository;
    final SoloInfoWebSocketHandler soloInfoWebSocketHandler;
    final InfoWebSocketHandler infoWebSocketHandler;
    final SystemData systemData;

    public PostWebSocketHandler(InfoRepository infoRepository, SoloInfoWebSocketHandler soloInfoWebSocketHandler, InfoWebSocketHandler infoWebSocketHandler, SystemData systemData) {
        this.infoRepository = infoRepository;
        this.soloInfoWebSocketHandler = soloInfoWebSocketHandler;
        this.infoWebSocketHandler = infoWebSocketHandler;
        this.systemData = systemData;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        macSessions.put(session, null);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        JSONObject json = new JSONObject(message.getPayload());
        if (macSessions.get(session) == null) {
            macSessions.put(session, json.getString("mac"));
            systemData.All.put(json.getString("mac"), new ArrayList<>());
        }
        systemData.addServerInfo(json);
        infoWebSocketHandler.sendOneInfo(json.toString());
        soloInfoWebSocketHandler.sendOneInfoSolo(json.toString());
        if (infoRepository.findByMac(json.getString("mac")) == null) {
            JSONObject jsonConfig = new JSONObject(json.toString());
            jsonConfig.put("cpuusage", 100);
            jsonConfig.getJSONObject("ram").put("usage", 100);
            for (int i = 0; i < jsonConfig.getJSONArray("gpuinfo").length(); i++) {
                jsonConfig.getJSONArray("gpuinfo").getJSONObject(i).put("load", 100);
            }
            for (int i = 0; i < jsonConfig.getJSONArray("discs").length(); i++) {
                jsonConfig.getJSONArray("discs").getJSONObject(i).put("usage", 100);
            }
            for (int i = 0; i < jsonConfig.getJSONArray("discsphysical").length(); i++) {
                jsonConfig.getJSONArray("discsphysical").getJSONObject(i).put("load", 100);
            }
            for (int i = 0; i < jsonConfig.getJSONArray("cpuinfo").length(); i++) {
                JSONArray array = jsonConfig.getJSONArray("cpuinfo").getJSONObject(i).getJSONArray("cores");
                for (int j = 0; j < array.length(); j++) {
                    array.getJSONObject(j).put("load", 100);
                    array.getJSONObject(j).put("temperature", 100);
                }
            }
            infoRepository.save(new Info(json.getString("mac"), jsonConfig.toString()));
        }
        systemData.addJsonByMac(json.getString("mac"), json);
        Info info = infoRepository.findByMac(json.getString("mac"));
        info.setOnline(true);
        infoRepository.save(info);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session,@NonNull  CloseStatus status) {
        Info info = infoRepository.findByMac(macSessions.get(session));
        info.setOnline(false);
        infoRepository.save(info);
        JSONObject json = systemData.All.get(macSessions.get(session)).get(systemData.All.get(macSessions.get(session)).size() - 1);
        systemData.addServerInfo(json);
        systemData.addJsonByMac(json.getString("mac"), json);
        infoWebSocketHandler.sendOneInfo(json.toString());
        soloInfoWebSocketHandler.sendOneInfoSolo(json.toString());
        macSessions.remove(session);
    }
}