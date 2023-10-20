package com.farneser.cloudfilestorage.controllers;

import com.farneser.cloudfilestorage.dto.RegisterDto;
import com.farneser.cloudfilestorage.exception.UserRegistrationException;
import com.farneser.cloudfilestorage.models.User;
import com.farneser.cloudfilestorage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String getLogin(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @GetMapping("/register")
    public String getRegister(Model model) {
        model.addAttribute("user", new RegisterDto());
        return "register";
    }

    @PostMapping("/register")
    public String postRegister(@ModelAttribute("user") RegisterDto registerDto) {
        try {
            var user = userService.registerNewUser(registerDto);

            System.out.println(user);
        } catch (UserRegistrationException e) {
            return "register";
        }

        return "redirect:/login";
    }
}
