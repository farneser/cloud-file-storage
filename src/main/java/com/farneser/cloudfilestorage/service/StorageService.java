package com.farneser.cloudfilestorage.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StorageService {
    void createFolder(String path);

    boolean createUserInitialFolder(long userId);

    void uploadFile(String currentPath, MultipartFile file);

    InputStream download(String fullPath);
}
