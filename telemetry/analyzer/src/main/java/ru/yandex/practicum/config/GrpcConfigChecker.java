package ru.yandex.practicum.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GrpcConfigChecker implements CommandLineRunner {

    private final Environment environment;

    public GrpcConfigChecker(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
        log.info("=== ПРОВЕРКА КОНФИГУРАЦИИ GRPC ===");

        String address = environment.getProperty("grpc.client.hub-router.address");
        String keepAlive = environment.getProperty("grpc.client.hub-router.enableKeepAlive");
        String negotiation = environment.getProperty("grpc.client.hub-router.negotiationType");

        log.info("grpc.client.hub-router.address = {}", address);
        log.info("grpc.client.hub-router.enableKeepAlive = {}", keepAlive);
        log.info("grpc.client.hub-router.negotiationType = {}", negotiation);

        if (address == null) {
            log.error("❌ Конфигурация gRPC не читается!");
        } else {
            log.info("✅ Конфигурация gRPC загружена");
        }
    }
}