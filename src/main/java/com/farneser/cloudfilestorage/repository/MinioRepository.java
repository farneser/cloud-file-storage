package com.farneser.cloudfilestorage.repository;

import com.farneser.cloudfilestorage.exception.InternalServerException;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
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
    @Value("${minio.bucket}")
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

    public boolean createFolder(String rawPath) {
        var folderPath = prettyFolderPath(rawPath);

        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(rootBucket)
                    .object(folderPath)
                    .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                    .build());

            return true;
        } catch (Exception e) {
            log.warn(e.getMessage());
            // FIXME: 10/23/23 handle errors
            return false;
        }
    }

    private String prettyFolderPath(String rawFolder) {
        if (!rawFolder.endsWith("/")) {
            return rawFolder + "/";
        }

        return rawFolder;
    }

    public void uploadFile(String fullPath, MultipartFile file) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(rootBucket)
                    .object(Paths.get(fullPath, file.getOriginalFilename()).toString())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

        } catch (Exception e) {
            log.warn(e.getMessage());
            // FIXME: 10/23/23 handle errors
        }
    }

    public InputStream downloadFile(String fullPath) {
        try {
            return minioClient.getObject(GetObjectArgs
                    .builder()
                    .bucket(rootBucket)
                    .object(fullPath)
                    .build());
        } catch (Exception e) {
            log.warn(e.getMessage());
            // FIXME: 10/23/23 no more return null
            return null;
        }
    }
}