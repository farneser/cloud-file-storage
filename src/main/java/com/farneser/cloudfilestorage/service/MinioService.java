package com.farneser.cloudfilestorage.service;

import com.farneser.cloudfilestorage.models.User;
import com.farneser.cloudfilestorage.repository.MinioRepository;
import com.farneser.cloudfilestorage.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class MinioService implements StorageService {
    private final MinioRepository minioRepository;

    @Autowired
    public MinioService(MinioRepository minioRepository) {
        this.minioRepository = minioRepository;
    }

    public void createBucket(String path) {
        String fullPath = getUserBucketPath() + path;
        minioRepository.createBucket(fullPath);
    }

    public boolean createUserInitialBucket(long userId) {
        return minioRepository.createBucket(UserUtils.getUserBucket(userId));
    }

    public void uploadFile(String bucketName, String objectName, MultipartFile file) {
        minioRepository.uploadFile(bucketName, objectName, file);
    }

    public InputStream download(String fullPath) {
        return minioRepository.downloadFile(getUserBucketPath() + fullPath);
    }

    private String getUserBucketPath() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userDetails = (User) authentication.getPrincipal();

        return UserUtils.getUserBucket(userDetails.getId());
    }
}