package com.farneser.cloudfilestorage.config.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Profile("default")
@Configuration
public class MinioConfig {
    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.access_key}")
    private String accessKey;
    @Value("${minio.secret_key}")
    private String secretKey;
    @Value("${minio.bucket}")
    private String bucket;

    @Bean
    public MinioClient minioClient() {
        var client = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();

        try {
            var found = false;

            found = client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());

            if (!found) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            } else {
                log.info("Bucket '" + bucket + "' already exists.");
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
                 | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException
                 | XmlParserException e) {
            throw new RuntimeException(e);
        }

        return client;
    }
}