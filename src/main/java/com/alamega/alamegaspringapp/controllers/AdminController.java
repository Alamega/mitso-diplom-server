package com.alamega.alamegaspringapp.controllers;

import com.alamega.alamegaspringapp.SystemData;
import com.alamega.alamegaspringapp.info.Info;
import com.alamega.alamegaspringapp.info.InfoRepository;
import com.alamega.alamegaspringapp.user.User;
import com.alamega.alamegaspringapp.user.UserRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("admin")
public class AdminController {
    final UserRepository userRepository;
    final InfoRepository infoRepository;
    final SystemData systemData;
    public AdminController(UserRepository userRepository, InfoRepository infoRepository, SystemData systemData) {
        this.userRepository = userRepository;
        this.infoRepository = infoRepository;
        this.systemData = systemData;
    }

    @GetMapping({"", "/"})
    public String admin() {
        return "admin/admin_panel";
    }

    @GetMapping( "/users")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAll().toArray());
        return "admin/users";
    }

    @GetMapping( "/params/{mac}")
    public String params(@PathVariable String mac, Model model) {
        Info info = infoRepository.findByMac(mac);
        if (info != null) {
            JSONObject json = new JSONObject(info.getConfig());
            model.addAttribute("config", json.toString());
            SystemData.addToModel(model, json);
        }
        return "admin/params";
    }


    @GetMapping( "/macs")
    public String macs(Model model) {
        List<String> macs = new ArrayList<>();
        infoRepository.findAll().forEach(info -> {
            macs.add(info.getMac());
        });
        model.addAttribute("macs", macs);
        return "admin/macs";
    }

    @PostMapping("/users/role/{id}")
    public String setUserRole(@PathVariable UUID id) {
        if (userRepository.findById(id).isPresent()) {
            User user = userRepository.findById(id).get();
            if (user.getRole().equals("USER")){
                user.setRole("ADMIN");
            } else {
                user.setRole("USER");
            }
            userRepository.save(user);
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable UUID id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }
}
