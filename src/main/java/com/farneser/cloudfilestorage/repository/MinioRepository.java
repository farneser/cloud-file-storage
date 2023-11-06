package com.farneser.cloudfilestorage.repository;

import com.farneser.cloudfilestorage.dto.FileDto;
import com.farneser.cloudfilestorage.exception.InternalServerException;
import com.farneser.cloudfilestorage.exception.MinioException;
import com.farneser.cloudfilestorage.utils.UserUtils;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class MinioRepository {

    private final MinioClient minioClient;
    @Value("${minio.bucket:user-files}")
    private String rootBucket;

    public MinioRepository(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public List<Item> getPathItems(long userId, String rawPath) throws InternalServerException {
        var path = prettyFolderPath(Paths.get(getUserFolder(userId), rawPath).toString());

        var result = new ArrayList<Item>();

        try {
            var itemList = minioClient.listObjects(ListObjectsArgs.builder().bucket(rootBucket).prefix(path).build());

            for (var item : itemList) {
                result.add(item.get());
            }
            log.info("Found " + result.size() + " objects");
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
                 | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException
                 | XmlParserException e) {
            log.error(e.getMessage());

            throw new InternalServerException("Error while reading files in path: " + path + ". Error: " + e.getMessage());
        }

        return result;
    }

    public void createFolder(long userId, String rawPath) throws MinioException {
        var path = Paths.get(getUserFolder(userId), rawPath).toString();

        try {
            minioClient.putObject(PutObjectArgs.builder().bucket(rootBucket).object(path).stream(new ByteArrayInputStream(new byte[0]), 0, -1).build());
            log.info("Created folder: " + rawPath);
        } catch (Exception e) {
            log.error(e.getMessage());

            throw new MinioException(e.getMessage());
        }
    }

    public void delete(String path) throws MinioException {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(rootBucket).object(path).build());
            log.info("Deleted object: " + path);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
                 | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException
                 | XmlParserException e) {
            log.error(e.getMessage());

            throw new MinioException(e.getMessage());
        }
    }

    public void deleteFolderRecursive(long userId, String rawPath) throws MinioException {
        var path = Paths.get(getUserFolder(userId), rawPath).toString();

        try {
            var objects = minioClient.listObjects(ListObjectsArgs.builder().bucket(rootBucket).recursive(true).prefix(path).build());

            for (var result : objects) {
                var item = result.get();

                // If item is a directory or if the path is not a directory and the path is equal to the item name
                // then delete the item

                if (isPathOverrideObject(item.isDir(), path, item.objectName())) {
                    this.delete(item.objectName());

                    log.info("Deleted object: " + item.objectName());
                }
            }

            this.delete(path);

            log.info("Deleted folder: " + path);

        } catch (Exception e) {
            log.error(e.getMessage());

            throw new MinioException(e.getMessage());
        }
    }

    private boolean isPathOverrideObject(boolean isDir, String path, String objectName) {
        return isDir || path.length() - 1 < objectName.lastIndexOf("/") || objectName.equals(path);
    }

    private String prettyFolderPath(String rawFolder) {
        if (!rawFolder.endsWith("/")) {
            return rawFolder + "/";
        }

        return rawFolder;
    }

    public void uploadFile(long userId, String rawFullPath, MultipartFile file) throws MinioException {
        var fullPath = Paths.get(getUserFolder(userId), rawFullPath).toString();

        try {
            minioClient.putObject(PutObjectArgs.builder().bucket(rootBucket).object(Paths.get(fullPath, file.getOriginalFilename()).toString()).stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build());

            log.info("Uploaded file: " + file.getOriginalFilename());
        } catch (Exception e) {
            log.error(e.getMessage());

            throw new MinioException(e.getMessage());
        }
    }

    public InputStream downloadFile(String fullPath) throws MinioException {
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(rootBucket).object(fullPath).build());
        } catch (Exception e) {
            log.error(e.getMessage());

            throw new MinioException("Error while getting file");
        }
    }

    public List<FileDto> download(long userId, String rawFullPath) throws MinioException {
        var result = new ArrayList<FileDto>();

        var fullPath = Paths.get(getUserFolder(userId), rawFullPath).toString();

        try {
            var files = minioClient.listObjects(ListObjectsArgs.builder().bucket(rootBucket).prefix(fullPath).recursive(true).build());

            for (var file : files) {
                var dto = new FileDto();

                dto.setFile(downloadFile(file.get().objectName()));

                dto.setFileName(Paths.get(file.get().objectName()).getFileName().toString());

                var slashIndex = file.get().objectName().indexOf("/");

                if (slashIndex != -1) {
                    var substring = file.get().objectName().substring(slashIndex);

                    dto.setPath(Paths.get(substring).getParent().toString());
                } else {
                    dto.setPath("/");
                }

                result.add(dto);

                log.info("Downloaded file: " + file.get().objectName());
            }

            return result;

        } catch (Exception e) {
            log.error(e.getMessage());

            throw new MinioException("Error while getting file");
        }
    }

    public List<Item> search(long userId, String query) throws InternalServerException {
        var result = new ArrayList<Item>();

        var itemList = minioClient.listObjects(ListObjectsArgs.builder().bucket(rootBucket).prefix(getUserFolder(userId)).recursive(true).build());

        try {
            for (var item : itemList) {
                if (item.get().objectName().contains(query)) {
                    result.add(item.get());
                }
            }

            log.info("Found " + result.size() + " objects");
        } catch (Exception e) {
            throw new InternalServerException("failed to search query");
        }

        return result;
    }

    public void rename(long userId, String path, String newName) throws InternalServerException {

        try {
            var originalPath = Paths.get(getUserFolder(userId), path);

            var newPath = originalPath.resolveSibling(newName);

            var items = minioClient.listObjects(ListObjectsArgs.builder().bucket(rootBucket).prefix(originalPath.toString()).recursive(true).build());

            items.forEach(item -> {
                try {
                    var newObjectPath = item.get().objectName().replace(originalPath.toString(), newPath.toString());

                    if (isPathOverrideObject(item.get().isDir(), originalPath.toString(), item.get().objectName())) {
                        minioClient.copyObject(CopyObjectArgs.builder().source(CopySource.builder().bucket(rootBucket).object(item.get().objectName()).build()).bucket(rootBucket).object(newObjectPath).build());

                        log.info("Renamed object: " + item.get().objectName() + " to: " + newObjectPath);

                        minioClient.removeObject(RemoveObjectArgs.builder().bucket(rootBucket).object(item.get().objectName()).build());

                        log.info("Deleted object: " + item.get().objectName());
                    }

                } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
                         | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException
                         | XmlParserException e) {
                    log.error(e.getMessage());
                }
            });

            log.info("Renamed object: " + path + " to: " + newPath);

        } catch (Exception e) {
            log.error(e.getMessage());

            throw new InternalServerException(e.getMessage());
        }
    }

    private String getUserFolder(long userId) {
        return UserUtils.getUserBucket(userId);
    }
}