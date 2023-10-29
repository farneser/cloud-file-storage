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
    public String postFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "path", defaultValue = "/") String path, RedirectAttributes redirectAttributes) {
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

    @PostMapping("/folder")
    public String postFolder(@RequestParam("folder") MultipartFile[] folder, @RequestParam(value = "path", defaultValue = "/") String path, RedirectAttributes redirectAttributes) {

        var message = "";

        try {
            for (var file : folder) {
                storageService.uploadFile(path, file);
            }

            message = "Folder uploaded successfully!";
        } catch (MinioException e) {
            message = "Uploading folder error";
        }

        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/?path=" + path;
    }

    @PostMapping("/folder/create")
    public String createFolder(@RequestParam("path") String path, @RequestParam("folderName") String folderName, RedirectAttributes redirectAttributes) {
        var message = "";

        try {
            storageService.createFolder(path, folderName);

            message = "Folder created successfully!";
        } catch (MinioException e) {
            log.error(e.getMessage());
            message = "Failed to create folder";
        }

        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/?path=" + path;
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("path") String path, @RequestParam("objectName") String objectName, RedirectAttributes redirectAttributes) {
        var message = "";

        try {
            storageService.delete(objectName);

            message = "Object deleted successfully!";
        } catch (MinioException e) {
            log.error(e.getMessage());
            message = "Failed to delete object";
        }

        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/?path=" + path;
    }

}