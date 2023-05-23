package com.alamega.alamegaspringapp.wsHandlers;

import com.alamega.alamegaspringapp.SystemData;
import com.alamega.alamegaspringapp.info.Info;
import com.alamega.alamegaspringapp.info.InfoRepository;
import com.alamega.alamegaspringapp.record.Record;
import com.alamega.alamegaspringapp.record.RecordRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

@Component
public class PostWebSocketHandler extends TextWebSocketHandler {
    private final HashMap<WebSocketSession, String> macSessions = new HashMap<>();

    final InfoRepository infoRepository;
    final RecordRepository recordRepository;
    final SoloInfoWebSocketHandler soloInfoWebSocketHandler;
    final InfoWebSocketHandler infoWebSocketHandler;
    final SystemData systemData;

    public PostWebSocketHandler(InfoRepository infoRepository, RecordRepository recordRepository, SoloInfoWebSocketHandler soloInfoWebSocketHandler, InfoWebSocketHandler infoWebSocketHandler, SystemData systemData) {
        this.infoRepository = infoRepository;
        this.recordRepository = recordRepository;
        this.soloInfoWebSocketHandler = soloInfoWebSocketHandler;
        this.infoWebSocketHandler = infoWebSocketHandler;
        this.systemData = systemData;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        macSessions.put(session, null);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session,@NonNull  TextMessage message) {
        String jsonString = message.getPayload();
        JSONObject json = new JSONObject(jsonString);
        infoWebSocketHandler.sendOneInfo(jsonString);
        soloInfoWebSocketHandler.sendOneInfoSolo(jsonString);
        if (infoRepository.findByMac(json.getString("mac")) == null) {
            JSONObject jsonConfig = new JSONObject(jsonString);
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
        systemData.All.put(json.getString("mac"), json);
        Info info = infoRepository.getReferenceById(json.getString("mac"));
        recordRepository.findAll().forEach(record -> {
            if (record.getDate().getTime() < new Date().getTime() - 1000 * 60 * 60) {
                recordRepository.delete(record);
            }
        });
        recordRepository.save(new Record(info, json.toString()));
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session,@NonNull  CloseStatus status) {
        macSessions.remove(session);
    }
}