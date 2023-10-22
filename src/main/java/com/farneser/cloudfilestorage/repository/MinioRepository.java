package com.farneser.cloudfilestorage.repository;

import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

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

    public boolean createBucket(String rawBucket) {
        var bucket = prettyBucketPath(rawBucket);

        try {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(rootBucket + "/" + bucket).build());
            return true;
        } catch (Exception e) {
            log.warn(e.getMessage());
            // FIXME: 10/23/23 handle errors
            return false;
        }
    }

    private String prettyBucketPath(String rawBucket) {
        if (!rawBucket.endsWith("/")) {
            return rawBucket + "/";
        }

        return rawBucket;
    }

    public void uploadFile(String bucketName, String objectName, MultipartFile file) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
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