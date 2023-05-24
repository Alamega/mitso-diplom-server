package com.alamega.alamegaspringapp.controllers;

import com.alamega.alamegaspringapp.SystemData;
import com.alamega.alamegaspringapp.info.Info;
import com.alamega.alamegaspringapp.info.InfoRepository;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@org.springframework.web.bind.annotation.RestController
public class RestController {
    final InfoRepository infoRepository;
    final SystemData systemData;

    public RestController(InfoRepository infoRepository, SystemData systemData) {
        this.infoRepository = infoRepository;
        this.systemData = systemData;
    }

    @PostMapping( "/admin/postConfig")
    public String postConfig(@RequestBody String data) {
        JSONObject json = new JSONObject(data);
        Info info = infoRepository.findByMac(json.getString("mac"));
        info.setConfig(data);
        infoRepository.save(info);
        return "YES";
    }

    @GetMapping( "/data/{mac}")
    public String getData(@PathVariable String mac) {
        List<JSONObject> input = systemData.All.get(mac);
        List<String> result = new ArrayList<>();
        if (input != null) {
            for (JSONObject json : input) {
                result.add(json.toString());
            }
        }
        return result.toString();
    }
}
