package com.alamega.alamegaspringapp.controllers;

import com.alamega.alamegaspringapp.user.User;
import com.alamega.alamegaspringapp.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserPageController {
    private final UserRepository userRepository;
    public UserPageController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping({"/me"})
    public String me() {
        return "redirect:/users/" + SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping({"/users/{username}"})
    public String user(Model model, @PathVariable String username) {
        User pageOwner = userRepository.findByUsername(username);
        User currentUser = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (pageOwner!=null) {
            model.addAttribute("pageOwner", pageOwner);
        } else {
            //Если искомого юзера не существует
            if (currentUser!=null) {
                //И текущий юзер авторизован
                return "redirect:/users/" + currentUser.getUsername();
            } else {
                //И текущий юзер - не авторизован
                return "redirect:/login";
            }
        }
        model.addAttribute("currentUser", currentUser);
        return "user";
    }
}
