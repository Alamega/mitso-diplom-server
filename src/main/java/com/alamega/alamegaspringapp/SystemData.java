package com.alamega.alamegaspringapp;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class SystemData {
    public Map<String, JSONObject> All = new HashMap<>();

    public static void addServerInfo(JSONObject jsonObject) {
        jsonObject.put("isonline", new Date().getTime() - Long.parseLong(jsonObject.get("time").toString()));
    }
}
