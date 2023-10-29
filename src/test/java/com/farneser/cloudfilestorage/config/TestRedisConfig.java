package com.farneser.cloudfilestorage.config;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.GenericContainer;

@Configuration
@Slf4j
@Profile("test")
public class TestRedisConfig {

    @Bean("redis-container")
    public GenericContainer<?> redisContainer() {
        var redisContainer = new GenericContainer<>("redis:latest")
                .withExposedPorts(6379)
                .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                        new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(19192), new ExposedPort(6379)))));

        redisContainer.start();

        return redisContainer;
    }

}
