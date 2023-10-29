package com.farneser.cloudfilestorage.config.minio;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.GenericContainer;

@Configuration
@Slf4j
@Profile("test")
public class TestMinioConfig {

    @Bean("minio-container")
    public GenericContainer<?> minioContainer() {
        var minioContainer = new GenericContainer<>("minio/minio:latest")
                .withExposedPorts(9000)
                .withEnv("MINIO_ROOT_USER", "youraccesskey")
                .withEnv("MINIO_ROOT_PASSWORD", "yoursecretkey")
                .withCommand("server", "/data")
                .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                        new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(19191), new ExposedPort(9000)))));

        minioContainer.start();

        minioContainer.withCommand("server /data");

        return minioContainer;
    }

    @Bean
    @DependsOn({"minio-container"})
    public MinioClient minioClient() {
        var client = MinioClient
                .builder()
                .endpoint("http://localhost:19191")
                .credentials("youraccesskey", "yoursecretkey")
                .build();

        try {
            waitForMinioServerToBeReady(client);

            client.makeBucket(MakeBucketArgs.builder().bucket("user-files").build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return client;
    }

    private void waitForMinioServerToBeReady(MinioClient client) throws InterruptedException {
        var attempts = 0;

        while (attempts < 60) {
            try {
                client.listBuckets();
                return;
            } catch (Exception e) {
                Thread.sleep(1000);
                attempts++;
            }
        }

        throw new RuntimeException("Failed to connect to the Minio server.");
    }
}