package com.farneser.cloudfilestorage.controller;

import com.farneser.cloudfilestorage.exception.EmptyQueryException;
import com.farneser.cloudfilestorage.exception.InternalServerException;
import com.farneser.cloudfilestorage.service.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/search")
public class SearchController {

    private final StorageService storageService;

    @Autowired
    public SearchController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping
    public String getPage(Model model, @RequestParam(value = "query") String path, RedirectAttributes redirectAttributes) {
        try {
            var items = storageService.searchItems(path);

            model.addAttribute("searchItems", items);

        } catch (InternalServerException | EmptyQueryException e) {
            log.error(e.getMessage());
            redirectAttributes.addAttribute("message", e.getMessage());
        }

        model.addAttribute("path", path);

        return "search";
    }
}
