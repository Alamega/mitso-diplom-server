package com.alamega.alamegaspringapp.controllers;

import com.alamega.alamegaspringapp.SystemData;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.alamega.alamegaspringapp.wsHandlers.InfoWebSocketHandler.webSocket;

@org.springframework.web.bind.annotation.RestController
public class RestController {
    @PostMapping("/data")
    public String postData(@RequestBody String body) {
        JSONObject json = new JSONObject(body);
        webSocket.sendOneInfo(body);
        SystemData.All.put(json.getString("mac"), json);
        return "Принято";
    }
}
