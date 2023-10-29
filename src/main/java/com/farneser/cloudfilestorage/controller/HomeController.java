package com.farneser.cloudfilestorage.controller;

import com.farneser.cloudfilestorage.dto.PathPartDto;
import com.farneser.cloudfilestorage.exception.InternalServerException;
import com.farneser.cloudfilestorage.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping({"/home", "/"})
public class HomeController {
    private final StorageService storageService;

    public HomeController(StorageService storageService) {
        this.storageService = storageService;
    }

    public static List<PathPartDto> getPath(String path) {

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        var pathParts = List.of(path.split("/"));

        var result = new ArrayList<PathPartDto>();

        var dto = new PathPartDto("/", "Root page /");

        result.add(dto);

        for (var i = 0; i < pathParts.size(); i++) {
            if (pathParts.get(i).isEmpty()) {
                continue;
            }

            result.add(new PathPartDto(pathParts.stream()
                    .limit(i + 1)
                    .collect(Collectors.joining("/")), pathParts.get(i) + "/"));
        }

        return result;
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
