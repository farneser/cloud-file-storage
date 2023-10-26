package com.farneser.cloudfilestorage.controller;

import com.farneser.cloudfilestorage.exception.InternalServerException;
import com.farneser.cloudfilestorage.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping({"/home", "/"})
public class HomeController {
    private final StorageService storageService;

    public HomeController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping()
    public String home(Model model, @RequestParam(value = "path", defaultValue = "/") String path) {

        try {
            var items = storageService.getPathItems(path);

            model.addAttribute("storageItems", items);

        } catch (InternalServerException e) {
            log.error(e.getMessage());
        }

        model.addAttribute("path", path);

        return "index";
    }
}
