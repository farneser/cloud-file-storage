package com.farneser.cloudfilestorage.service;

import com.farneser.cloudfilestorage.dto.FileDto;
import com.farneser.cloudfilestorage.dto.StorageDto;
import com.farneser.cloudfilestorage.exception.InternalServerException;
import com.farneser.cloudfilestorage.exception.MinioException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageService {
    void createFolder(String path, String folderName) throws MinioException;

    List<StorageDto> getPathItems(String path) throws InternalServerException;

    void createUserInitialFolder(long userId) throws MinioException;

    void uploadFile(String currentPath, MultipartFile file) throws MinioException;

    FileDto download(String fullPath) throws MinioException, InternalServerException;

    void delete(String path) throws MinioException;
}
