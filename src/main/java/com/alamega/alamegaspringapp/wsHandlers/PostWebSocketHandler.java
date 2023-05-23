package com.alamega.alamegaspringapp.wsHandlers;

import com.alamega.alamegaspringapp.SystemData;
import com.alamega.alamegaspringapp.info.Info;
import com.alamega.alamegaspringapp.info.InfoRepository;
import com.alamega.alamegaspringapp.record.Record;
import com.alamega.alamegaspringapp.record.RecordRepository;
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
        if (!systemData.All.containsKey(json.getString("mac"))) {
            infoRepository.save(new Info(json.getString("mac"), jsonString));
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