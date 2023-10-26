package com.farneser.cloudfilestorage;

import com.farneser.cloudfilestorage.exception.InternalServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.DatagramSocket;
import java.net.InetAddress;

@Slf4j
@Component
public class AppStartupRunner implements ApplicationRunner {
    @Value("${server.port}")
    private Integer port;

    private String getAddress() throws InternalServerException {
        try (final var datagramSocket = new DatagramSocket()) {
            datagramSocket.connect(InetAddress.getByName("1.1.1.1"), 80);

            return datagramSocket.getLocalAddress().getHostAddress();
        } catch (Exception e) {
            throw new InternalServerException("Remote address not found");
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        var servers = 2;

        log.info("Server running local:\thttp://localhost:" + port);

        try {
            log.info("Server running remote:\thttp://" + getAddress() + ":" + port);
        } catch (InternalServerException e) {
            servers--;
        }

        log.info("Status: " + servers + " url available");
    }
}