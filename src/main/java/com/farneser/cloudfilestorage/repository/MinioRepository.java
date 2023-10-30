package com.farneser.cloudfilestorage.repository;

import com.farneser.cloudfilestorage.dto.FileDto;
import com.farneser.cloudfilestorage.exception.InternalServerException;
import com.farneser.cloudfilestorage.exception.MinioException;
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

    public List<Item> getPathItems(String path) throws InternalServerException {
        path = prettyFolderPath(path);

        var result = new ArrayList<Item>();

        try {
            var itemList = minioClient.listObjects(ListObjectsArgs.builder().bucket(rootBucket).prefix(path).build());

            for (var item : itemList) {
                result.add(item.get());
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
                 | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException
                 | XmlParserException e) {
            log.error(e.getMessage());

            throw new InternalServerException("Error while reading files in path: " + path + ". Error: " + e.getMessage());
        }

        return result;
    }

    public void createFolder(String rawPath) throws MinioException {
        try {
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket(rootBucket)
                    .object(rawPath)
                    .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                    .build());

        } catch (Exception e) {
            log.error(e.getMessage());

            throw new MinioException(e.getMessage());
        }
    }

    public void delete(String path) throws MinioException {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(rootBucket).object(path).build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
                 | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException
                 | XmlParserException e) {
            log.error(e.getMessage());

            throw new MinioException(e.getMessage());
        }
    }

    public void deleteFolderRecursive(String path) throws MinioException {
        try {
            var objects = minioClient.listObjects(ListObjectsArgs.builder().bucket(rootBucket).recursive(true).prefix(path).build());

            for (var result : objects) {
                var item = result.get();

                this.delete(item.objectName());
                log.info("Deleted object: " + item.objectName());
            }

            this.delete(path);

            log.info("Deleted folder: " + path);

        } catch (Exception e) {
            log.error(e.getMessage());

            throw new MinioException(e.getMessage());
        }
    }

    private String prettyFolderPath(String rawFolder) {
        if (!rawFolder.endsWith("/")) {
            return rawFolder + "/";
        }

        return rawFolder;
    }

    public void uploadFile(String fullPath, MultipartFile file) throws MinioException {
        try {
            minioClient.putObject(PutObjectArgs.builder().bucket(rootBucket).object(Paths.get(fullPath, file.getOriginalFilename()).toString()).stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build());

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

    public List<FileDto> download(String fullPath) throws MinioException {
        var result = new ArrayList<FileDto>();

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
            }

            return result;

        } catch (Exception e) {
            log.error(e.getMessage());

            throw new MinioException("Error while getting file");
        }
    }

    public List<Item> search(String userFolderPath, String query) throws InternalServerException {
        var result = new ArrayList<Item>();

        var itemList = minioClient.listObjects(ListObjectsArgs.builder().bucket(rootBucket).prefix(userFolderPath).recursive(true).build());

        try {
            for (var item : itemList) {
                if (item.get().objectName().contains(query)) {
                    result.add(item.get());
                }
            }
        } catch (Exception e) {
            throw new InternalServerException("failed to search query");
        }

        return result;
    }

    public void rename(String path, String newName) throws InternalServerException {

        try {
            minioClient.copyObject(CopyObjectArgs.builder()
                    .source(CopySource.builder().bucket(rootBucket).object(path).build())
                    .bucket(rootBucket)
                    .object(newName)
                    .build());


            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(rootBucket)
                    .object(path)
                    .build());

            System.out.println("Object renamed successfully");

        } catch (Exception e) {
            log.error(e.getMessage());

            throw new InternalServerException(e.getMessage());
        }

    }
}