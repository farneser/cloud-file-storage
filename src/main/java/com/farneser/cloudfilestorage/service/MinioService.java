package com.farneser.cloudfilestorage.service;

import com.farneser.cloudfilestorage.dto.FileDto;
import com.farneser.cloudfilestorage.dto.StorageDto;
import com.farneser.cloudfilestorage.exception.EmptyQueryException;
import com.farneser.cloudfilestorage.exception.InternalServerException;
import com.farneser.cloudfilestorage.exception.MinioException;
import com.farneser.cloudfilestorage.models.User;
import com.farneser.cloudfilestorage.repository.MinioRepository;
import com.farneser.cloudfilestorage.utils.InputStreamUtils;
import com.farneser.cloudfilestorage.utils.MinioUtils;
import com.farneser.cloudfilestorage.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
public class MinioService implements StorageService {
    private static final String FOLDER_STATIC_FILE_NAME = "folder.ini";
    private final MinioRepository minioRepository;

    @Autowired
    public MinioService(MinioRepository minioRepository) {
        this.minioRepository = minioRepository;
    }

    public void createFolder(String path, String folderName) throws MinioException {
        var fullPath = Paths.get(getUserFolderPath(), path, folderName, FOLDER_STATIC_FILE_NAME).toString();
        minioRepository.createFolder(fullPath);
    }

    @Override
    public List<StorageDto> getPathItems(String path) throws InternalServerException {
        var items = minioRepository.getPathItems(Paths.get(getUserFolderPath(), path).toString());

        return MinioUtils.convertItemToStorageDto(items, FOLDER_STATIC_FILE_NAME);
    }

    @Override
    public List<StorageDto> searchItems(String query) throws InternalServerException, EmptyQueryException {

        if (query == null || query.isEmpty() || query.equals("/")) {
            throw new EmptyQueryException("query: " + query + " is empty");
        }

        var items = minioRepository.search(getUserFolderPath(), query);

        return MinioUtils.convertItemToStorageDto(items, FOLDER_STATIC_FILE_NAME);
    }

    public void createUserInitialFolder(long userId) throws MinioException {
        minioRepository.createFolder(Paths.get(UserUtils.getUserBucket(userId), FOLDER_STATIC_FILE_NAME).toString());
    }

    public void uploadFile(String currentPath, MultipartFile file) throws MinioException {
        minioRepository.uploadFile(Paths.get(getUserFolderPath(), currentPath).toString(), file);
    }

    public FileDto download(String fullPath) throws MinioException, InternalServerException {
        var result = new FileDto();

        result.setFileName(Paths.get(fullPath).getFileName().toString() + ".zip");

        try {
            var files = minioRepository.download(Paths.get(getUserFolderPath(), fullPath).toString());

            // here I decided to leave the folder.ini files for download in order to preserve the folder structure

            if (files.isEmpty()) {
                throw new InternalServerException("Not found");
            }

            if (files.size() == 1 && !files.get(0).getFileName().equals(FOLDER_STATIC_FILE_NAME)) {
                return files.get(0);
            }

            result.setFile(InputStreamUtils.compressToZip(files));
        } catch (IOException e) {
            log.error(e.getMessage());

            throw new InternalServerException("Failed to get files");
        }

        result.setPath("/");

        return result;
    }

    @Override
    public void delete(String path) throws MinioException {
        minioRepository.deleteFolderRecursive(Paths.get(getUserFolderPath(), path).toString());
    }

    @Override
    public void rename(String path, String newName) throws InternalServerException {
        minioRepository.rename(Paths.get(getUserFolderPath(), path).toString(), newName);
    }

    private String getUserFolderPath() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userDetails = (User) authentication.getPrincipal();

        return UserUtils.getUserBucket(userDetails.getId());
    }
}