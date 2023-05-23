package com.alamega.alamegaspringapp.controllers;

import com.alamega.alamegaspringapp.info.Info;
import com.alamega.alamegaspringapp.info.InfoRepository;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("admin")
public class RestController {
    final InfoRepository infoRepository;

    public RestController(InfoRepository infoRepository) {
        this.infoRepository = infoRepository;
    }

    @PostMapping( "/postConfig")
    public String postConfig(@RequestBody String data) {
        JSONObject json = new JSONObject(data);
        Info info = infoRepository.findByMac(json.getString("mac"));
        info.setConfig(data);
        infoRepository.save(info);
        return "YES";
    }
}
