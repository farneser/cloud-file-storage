package com.farneser.cloudfilestorage.controller;

import com.farneser.cloudfilestorage.exception.MinioException;
import com.farneser.cloudfilestorage.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
public class StorageController {
    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping
    public ResponseEntity<InputStreamResource> download(@RequestParam("path") String path) {
        try {
            var fileDto = storageService.download(path);
            var headers = new HttpHeaders();

            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileDto.getFileName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(fileDto.getFile()));
        } catch (MinioException e) {
            return ResponseEntity.status(500).body(null);
        }
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