package com.alamega.alamegaspringapp;

import com.alamega.alamegaspringapp.info.InfoRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SystemData {
    public static Map<String, JSONObject> All = new HashMap<>();

    public static void addServerInfo(JSONObject jsonObject) {
        jsonObject.put("isonline", new Date().getTime() - Long.parseLong(jsonObject.get("time").toString()));
    }
}
