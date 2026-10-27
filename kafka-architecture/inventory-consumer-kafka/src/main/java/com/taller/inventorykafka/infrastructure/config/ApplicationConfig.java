package com.taller.inventorykafka.infrastructure.config;

import com.taller.inventorykafka.application.port.in.ConsumeOrderCreatedUseCase;
import com.taller.inventorykafka.application.port.out.InventoryReservation;
import com.taller.inventorykafka.application.usecase.ConsumeOrderCreatedService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    ConsumeOrderCreatedUseCase consumeOrderCreatedUseCase(InventoryReservation inventoryReservation) {
        return new ConsumeOrderCreatedService(inventoryReservation);
    }
}
