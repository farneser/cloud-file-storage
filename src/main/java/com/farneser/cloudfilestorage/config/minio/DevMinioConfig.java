package com.farneser.cloudfilestorage.config.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Profile("dev")
@Configuration
public class DevMinioConfig {
    @Bean
    public MinioClient minioClient() {
        var client = MinioClient.builder().endpoint("http://localhost:9000").credentials("youraccesskey", "yoursecretkey").build();

        try {
            var found = false;

            found = client.bucketExists(BucketExistsArgs.builder().bucket("user-files").build());

            if (!found) {
                client.makeBucket(MakeBucketArgs.builder().bucket("user-files").build());
            } else {
                log.info("Bucket 'user-files' already exists.");
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
                 | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException
                 | XmlParserException e) {
            throw new RuntimeException(e);
        }

        return client;
    }
}