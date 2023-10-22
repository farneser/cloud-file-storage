package com.farneser.cloudfilestorage.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StorageService {
    void createBucket(String path);

    void createUserInitialBucket(long userId);

    void uploadFile(String bucketName, String objectName, MultipartFile file);

    InputStream download(String fullPath);
}
