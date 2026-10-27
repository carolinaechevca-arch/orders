package com.taller.billingkafka.infrastructure.config;

import com.taller.billingkafka.application.port.in.ConsumeOrderCreatedUseCase;
import com.taller.billingkafka.application.port.out.InvoiceCreator;
import com.taller.billingkafka.application.usecase.ConsumeOrderCreatedService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    ConsumeOrderCreatedUseCase consumeOrderCreatedUseCase(InvoiceCreator invoiceCreator) {
        return new ConsumeOrderCreatedService(invoiceCreator);
    }
}
