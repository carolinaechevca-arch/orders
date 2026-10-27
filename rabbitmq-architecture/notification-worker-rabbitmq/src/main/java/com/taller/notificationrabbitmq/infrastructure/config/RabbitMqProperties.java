package com.taller.notificationrabbitmq.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbitmq")
public record RabbitMqProperties(String exchange, String queue, String routingKey) {
}
