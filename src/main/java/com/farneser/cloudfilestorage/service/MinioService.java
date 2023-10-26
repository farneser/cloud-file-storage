package com.farneser.cloudfilestorage.service;

import com.farneser.cloudfilestorage.dto.FileDto;
import com.farneser.cloudfilestorage.dto.StorageDto;
import com.farneser.cloudfilestorage.exception.InternalServerException;
import com.farneser.cloudfilestorage.exception.MinioException;
import com.farneser.cloudfilestorage.models.User;
import com.farneser.cloudfilestorage.repository.MinioRepository;
import com.farneser.cloudfilestorage.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MinioService implements StorageService {
    private final MinioRepository minioRepository;

    @Autowired
    public MinioService(MinioRepository minioRepository) {
        this.minioRepository = minioRepository;
    }

    public void createFolder(String path) throws MinioException {
        String fullPath = getUserFolderPath() + path;
        minioRepository.createFolder(fullPath);
    }

    @Override
    public List<StorageDto> getPathItems(String path) throws InternalServerException {
        var items = minioRepository.getPathItems(Paths.get(getUserFolderPath(), path).toString());

        var result = new ArrayList<StorageDto>();

        for (var item : items) {
            var storageDto = new StorageDto();

            storageDto.setItemPath(trimFromFirstSlash(item.objectName()));

            storageDto.setDir(item.isDir());

            result.add(storageDto);
        }

        return result;
    }

    public void createUserInitialFolder(long userId) throws MinioException {
        minioRepository.createFolder(UserUtils.getUserBucket(userId));
    }

    public void uploadFile(String currentPath, MultipartFile file) throws MinioException {
        minioRepository.uploadFile(Paths.get(getUserFolderPath(), currentPath).toString(), file);
    }

    public FileDto download(String fullPath) throws MinioException {
        var result = new FileDto();

        result.setFileName(Paths.get(fullPath).getFileName().toString());

        result.setFile(minioRepository.downloadFile(getUserFolderPath() + fullPath));

        return result;
    }

    private String getUserFolderPath() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userDetails = (User) authentication.getPrincipal();

        return UserUtils.getUserBucket(userDetails.getId());
    }

    private String trimFromFirstSlash(String path) {
        var slashIndex = path.indexOf('/');

        if (slashIndex != -1) {
            return path.substring(slashIndex);
        }

        return path;
    }
}