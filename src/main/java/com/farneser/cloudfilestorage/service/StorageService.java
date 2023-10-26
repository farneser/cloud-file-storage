package com.farneser.cloudfilestorage.service;

import com.farneser.cloudfilestorage.dto.StorageDto;
import com.farneser.cloudfilestorage.exception.InternalServerException;
import com.farneser.cloudfilestorage.exception.MinioException;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface StorageService {
    void createFolder(String path) throws MinioException;

    List<StorageDto> getPathItems(String path) throws InternalServerException;

    void createUserInitialFolder(long userId) throws MinioException;

    void uploadFile(String currentPath, MultipartFile file);

    InputStream download(String fullPath);
}
