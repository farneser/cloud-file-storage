package com.farneser.cloudfilestorage.controller;

import com.farneser.cloudfilestorage.dto.RegisterDto;
import com.farneser.cloudfilestorage.exception.MinioException;
import com.farneser.cloudfilestorage.exception.UserRegistrationException;
import com.farneser.cloudfilestorage.models.User;
import com.farneser.cloudfilestorage.service.UserService;
import com.farneser.cloudfilestorage.service.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
public class AuthController {

    private final UserService userService;
    private final StorageService storageService;

    @Autowired
    public AuthController(UserService userService, StorageService storageService) {
        this.userService = userService;
        this.storageService = storageService;
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
    public String postRegister(@ModelAttribute("user") RegisterDto registerDto, RedirectAttributes redirectAttributes) {
        try {
            var user = userService.registerNewUser(registerDto);

            log.info("created a new user - " + user.toString());

            storageService.createUserInitialFolder(user.getId());

            log.info("created a user bucket - " + user.getId());
        } catch (UserRegistrationException | MinioException e) {
            log.info(e.getMessage());

            redirectAttributes.addFlashAttribute("message", e.getMessage());

            return "redirect:/register";
        }
        return "redirect:/login";
    }
}
