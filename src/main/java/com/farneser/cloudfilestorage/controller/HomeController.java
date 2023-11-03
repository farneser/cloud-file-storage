package com.farneser.cloudfilestorage.controller;

import com.farneser.cloudfilestorage.dto.PathPartDto;
import com.farneser.cloudfilestorage.exception.InternalServerException;
import com.farneser.cloudfilestorage.service.storage.StorageService;
import com.farneser.cloudfilestorage.utils.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequestMapping({"/home", "/"})
public class HomeController {
    private final StorageService storageService;

    public HomeController(StorageService storageService) {
        this.storageService = storageService;
    }

    public static List<PathPartDto> getPath(String path) {
        return UrlUtils.getPathParts(path);
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
        model.addAttribute("pathParts", getPath(path));

        return "index";
    }
}
