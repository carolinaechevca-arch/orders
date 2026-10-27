package com.taller.ordersrabbitmq.infrastructure.config;

import com.taller.ordersrabbitmq.application.port.in.CreateOrderUseCase;
import com.taller.ordersrabbitmq.application.port.out.OrderMessagePublisher;
import com.taller.ordersrabbitmq.application.usecase.CreateOrderService;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    CreateOrderUseCase createOrderUseCase(OrderMessagePublisher publisher, Clock clock) {
        return new CreateOrderService(publisher, clock);
    }
}
