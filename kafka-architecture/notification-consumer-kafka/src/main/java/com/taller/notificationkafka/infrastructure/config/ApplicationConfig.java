package com.taller.notificationkafka.infrastructure.config;

import com.taller.notificationkafka.application.port.in.ConsumeOrderCreatedUseCase;
import com.taller.notificationkafka.application.port.out.NotificationSender;
import com.taller.notificationkafka.application.usecase.ConsumeOrderCreatedService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    ConsumeOrderCreatedUseCase consumeOrderCreatedUseCase(NotificationSender notificationSender) {
        return new ConsumeOrderCreatedService(notificationSender);
    }
}
