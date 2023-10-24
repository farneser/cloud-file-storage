package com.farneser.cloudfilestorage.controller;

import com.farneser.cloudfilestorage.service.StorageService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/storage")
public class FilesController {
    private final StorageService minioService;

    public FilesController(StorageService storageService) {
        this.minioService = storageService;
    }

    @GetMapping("/files")
    public String get() {
        return "upload_form";
    }

    @PostMapping("/files")
    public String post(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, HttpSession session) {
        minioService.uploadFile(getCurrentPath(session), file);
        redirectAttributes.addFlashAttribute("message", "File uploaded successfully!");
        return "redirect:/storage/files";
    }

    private static String getCurrentPath(HttpSession session) {
        var path = session.getAttribute("path");

        if (path == null) {
            return "/";
        }

        return path.toString();
    }

}