package com.alamega.alamegaspringapp.controllers;

import com.alamega.alamegaspringapp.SystemData;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MainController {
    @GetMapping({"/", "index", "index.html"})
    public String index() {
        return "index";
    }

    @GetMapping({"mac/{macAddress}"})
    public String info(@PathVariable String macAddress, Model model) {
        JSONObject json = SystemData.All.get(macAddress);
        model.addAttribute("json", json);
        return "info";
    }
}
