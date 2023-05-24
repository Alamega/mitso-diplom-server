package com.alamega.alamegaspringapp.controllers;

import com.alamega.alamegaspringapp.SystemData;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MainController {
    final SystemData systemData;

    public MainController(SystemData systemData) {
        this.systemData = systemData;
    }
    @GetMapping({"/", "index", "index.html"})
    public String index() {
        return "index";
    }

    @GetMapping({"mac/{macAddress}"})
    public String info(@PathVariable String macAddress, Model model) {
        if (systemData.All.containsKey(macAddress)) {
            JSONObject json =  systemData.All.get(macAddress).get(systemData.All.get(macAddress).size() - 1);
            SystemData.addToModel(model, json);
            return "info";
        } else {
            return "index";
        }
    }
}
