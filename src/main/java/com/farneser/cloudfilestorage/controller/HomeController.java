package com.farneser.cloudfilestorage.controller;

import com.farneser.cloudfilestorage.exception.InternalServerException;
import com.farneser.cloudfilestorage.service.StorageService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping({"/home", "/", "/dashboard"})
public class HomeController extends BaseController {
    private final StorageService storageService;

    public HomeController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping()
    public String home(Model model, HttpSession session) {
        try {
            var items = storageService.getPathItems(getCurrentPath(session));

            model.addAttribute("storageItems", items);

        } catch (InternalServerException e) {
            log.error(e.getMessage());
        }

        return "index";
    }
}
