package com.taller.orderskafka.infrastructure.config;

import com.taller.orderskafka.application.port.in.CreateOrderUseCase;
import com.taller.orderskafka.application.port.out.OrderEventPublisher;
import com.taller.orderskafka.application.usecase.CreateOrderService;
import java.time.Clock;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class ApplicationConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    CreateOrderUseCase createOrderUseCase(OrderEventPublisher publisher, Clock clock) {
        return new CreateOrderService(publisher, clock);
    }

    @Bean
    NewTopic ordersTopic(KafkaProperties properties) {
        return TopicBuilder.name(properties.topic()).partitions(properties.partitions()).replicas(1).build();
    }
}
