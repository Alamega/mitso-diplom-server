package com.alamega.alamegaspringapp.controllers;

import com.alamega.alamegaspringapp.post.Post;
import com.alamega.alamegaspringapp.post.PostRepository;
import com.alamega.alamegaspringapp.user.User;
import com.alamega.alamegaspringapp.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class UserPageController {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    public UserPageController(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
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
            List<Post> posts = postRepository.findAllByAuthorOrderByDateDesc(pageOwner);
            for (Post post : posts) {
                post.setText(post.getText().replaceAll("\n", "<br/>"));
            }
            model.addAttribute("posts", posts);
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

    @PostMapping("/posts")
    public String newPost(@ModelAttribute("text") String text){
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user!=null) {
            if (text.length() >= 1 && text.length() <= 1024) {
                postRepository.save(new Post(user, text));
            }
            return "redirect:/users/" + user.getUsername();
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable UUID id){
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent() && (user.getRole().equals("ADMIN") || user.getId().equals(post.get().getAuthor().getId()))) {
            postRepository.deleteById(id);
            return "redirect:/users/" + post.get().getAuthor().getUsername();
        } else {
            return "redirect:/login";
        }
    }
}
