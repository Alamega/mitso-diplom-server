package com.alamega.alamegaspringapp.controllers;

import com.alamega.alamegaspringapp.user.User;
import com.alamega.alamegaspringapp.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AuthController {
    private final UserRepository userRepository;
    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String login() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() != "anonymousUser") {
            return "redirect:/users/" + SecurityContextHolder.getContext().getAuthentication().getName();
        } else {
            return "auth/login";
        }
    }

    @GetMapping("/registration")
    public String registration() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() != "anonymousUser") {
            return "redirect:/users/" + SecurityContextHolder.getContext().getAuthentication().getName();
        } else {
            return "auth/registration";
        }
    }

    @PostMapping("/registration")
    public String addUser(Model model, @ModelAttribute("username") String username, @ModelAttribute("password") String password) {
        List<String> errors = new ArrayList<>();

        //Гениальная валидация
        if (userRepository.findByUsername(username) != null){ errors.add("Пользователь с таким никнеймом уже существует!"); }
        if (username.length()<4){ errors.add("Имя пользователя должно содержать минимум 4 символа!"); }
        if (username.length()>20){ errors.add("Имя пользователя должно содержать не более 20 символов!"); }
        if (password.length()<8){ errors.add("Пароль должен содержать минимум 8 символов!"); }
        if (password.length()>20){ errors.add("Пароль должен содержать не более 20 символов!"); }

        //Если ошибок нету
        if (errors.isEmpty()){
            User user = new User();
            user.setUsername(username);
            user.setPassword(new BCryptPasswordEncoder().encode(password));
            //Ну с таким никнеймом...
            if (username.equals("Alamega")) {
                user.setRole("ADMIN");
            } else {
                user.setRole("USER");
            }
            user.setEnabled(true);
            userRepository.save(user);
            model.addAttribute("result", "Регистрация прошла успешно!");
        }

        model.addAttribute("errors", errors);
        return "auth/registration";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest httpServletRequest) throws ServletException {
        httpServletRequest.logout();
        return "redirect:/";
    }
}
