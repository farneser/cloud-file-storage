package com.farneser.cloudfilestorage.service;

import com.farneser.cloudfilestorage.models.User;
import com.farneser.cloudfilestorage.repository.MinioRepository;
import com.farneser.cloudfilestorage.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Paths;

@Service
public class MinioService implements StorageService {
    private final MinioRepository minioRepository;

    @Autowired
    public MinioService(MinioRepository minioRepository) {
        this.minioRepository = minioRepository;
    }

    public void createFolder(String path) {
        String fullPath = getUserFolderPath() + path;
        minioRepository.createFolder(fullPath);
    }

    public boolean createUserInitialFolder(long userId) {
        return minioRepository.createFolder(UserUtils.getUserBucket(userId));
    }

    public void uploadFile(String currentPath, MultipartFile file) {
        minioRepository.uploadFile(Paths.get(getUserFolderPath(), currentPath).toString(), file);
    }

    public InputStream download(String fullPath) {
        return minioRepository.downloadFile(getUserFolderPath() + fullPath);
    }

    private String getUserFolderPath() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userDetails = (User) authentication.getPrincipal();

        return UserUtils.getUserBucket(userDetails.getId());
    }
}