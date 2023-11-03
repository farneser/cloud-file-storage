package com.farneser.cloudfilestorage.service.storage;

import com.farneser.cloudfilestorage.dto.FileDto;
import com.farneser.cloudfilestorage.dto.SearchDto;
import com.farneser.cloudfilestorage.dto.StorageDto;
import com.farneser.cloudfilestorage.exception.EmptyQueryException;
import com.farneser.cloudfilestorage.exception.InternalServerException;
import com.farneser.cloudfilestorage.exception.MinioException;
import com.farneser.cloudfilestorage.exception.NotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageService {
    void createFolder(String path, String folderName) throws MinioException;

    List<StorageDto> getPathItems(String path) throws InternalServerException;

    List<SearchDto> searchItems(String query) throws InternalServerException, EmptyQueryException;

    void createUserInitialFolder(long userId) throws MinioException;

    void uploadFile(String currentPath, MultipartFile file) throws MinioException;

    FileDto download(String fullPath) throws MinioException, InternalServerException, NotFoundException;

    void delete(String path) throws MinioException;

    void rename(String path, String newName) throws InternalServerException;
}
