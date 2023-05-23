package com.alamega.alamegaspringapp;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class SystemData {
    public Map<String, JSONObject> All = new HashMap<>();

    public static void addServerInfo(JSONObject jsonObject) {
        jsonObject.put("isonline", new Date().getTime() - Long.parseLong(jsonObject.get("time").toString()));
    }

    public static void addToModel(Model model, JSONObject json) {
        model.addAttribute("mac",  json.get("mac"));
        model.addAttribute("cpuusage",  json.get("cpuusage"));
        model.addAttribute("cores",  json.get("cores"));
        model.addAttribute("os",  json.get("os"));
        model.addAttribute("username",  json.get("username"));
        model.addAttribute("ramusage",  ((JSONObject)json.get("ram")).get("usage"));
        model.addAttribute("ramtotal",  ((JSONObject)json.get("ram")).get("total"));
        model.addAttribute("discs",  json.get("discs"));
        model.addAttribute("discsphysical",  json.get("discsphysical"));
        model.addAttribute("cpuinfo",  json.get("cpuinfo"));
        model.addAttribute("gpuinfo",  json.get("gpuinfo"));
    }
}
