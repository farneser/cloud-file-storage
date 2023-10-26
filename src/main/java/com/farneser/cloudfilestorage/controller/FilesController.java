package com.farneser.cloudfilestorage.controller;

import com.farneser.cloudfilestorage.exception.MinioException;
import com.farneser.cloudfilestorage.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/storage")
public class FilesController {
    private final StorageService storageService;

    public FilesController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/file")
    public String post(@RequestParam("file") MultipartFile file, @RequestParam(value = "path", defaultValue = "/") String path, RedirectAttributes redirectAttributes) {
        var message = "";

        try {
            storageService.uploadFile(path, file);

            message = "File uploaded successfully!";
        } catch (MinioException e) {
            message = "Uploading file error";
        }

        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/?path=" + path;
    }
}