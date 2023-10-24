package com.farneser.cloudfilestorage.repository;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Paths;

@Slf4j
@Repository
public class MinioRepository {

    private final MinioClient minioClient;
    @Value("${minio.bucket}")
    private String rootBucket;

    @SneakyThrows
    public MinioRepository(MinioClient minioClient) {
        this.minioClient = minioClient;
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