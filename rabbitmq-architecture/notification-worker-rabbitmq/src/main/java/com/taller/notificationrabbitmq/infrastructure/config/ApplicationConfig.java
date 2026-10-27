package com.taller.notificationrabbitmq.infrastructure.config;

import com.taller.notificationrabbitmq.application.port.in.ProcessOrderUseCase;
import com.taller.notificationrabbitmq.application.port.out.NotificationSender;
import com.taller.notificationrabbitmq.application.usecase.ProcessOrderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    ProcessOrderUseCase processOrderUseCase(NotificationSender notificationSender) {
        return new ProcessOrderService(notificationSender);
    }
}
