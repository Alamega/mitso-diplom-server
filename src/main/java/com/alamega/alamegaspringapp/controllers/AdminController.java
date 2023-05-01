package com.alamega.alamegaspringapp.controllers;

import com.alamega.alamegaspringapp.user.User;
import com.alamega.alamegaspringapp.user.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("admin")
public class AdminController {
    final
    UserRepository userRepository;
    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
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
