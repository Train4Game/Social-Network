package com.train4game.social.web.controllers;

import com.train4game.social.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @Autowired
    private UserService service;

    @GetMapping("/")
    public String root(Model model) {
        model.addAttribute("users", service.getAll());
        return "users";
    }
}
