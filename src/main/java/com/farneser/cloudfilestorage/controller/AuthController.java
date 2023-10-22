package com.farneser.cloudfilestorage.controller;

import com.farneser.cloudfilestorage.dto.RegisterDto;
import com.farneser.cloudfilestorage.exception.UserRegistrationException;
import com.farneser.cloudfilestorage.models.User;
import com.farneser.cloudfilestorage.service.MinioService;
import com.farneser.cloudfilestorage.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class AuthController {

    private final UserService userService;
    private final MinioService minioService;

    @Autowired
    public AuthController(UserService userService, MinioService minioService) {
        this.userService = userService;
        this.minioService = minioService;
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

            log.info("created a new user - " + user.toString());

            if (minioService.createUserInitialBucket(user.getId())) {
                // FIXME: 10/23/23 throw exceptions
                log.info("created a user bucket - " + user.getId());
            } else {
                log.info("failed to crate user bucket - " + user.getId());
            }
        } catch (UserRegistrationException e) {
            return "register";
        }

        return "redirect:/login";
    }
}
