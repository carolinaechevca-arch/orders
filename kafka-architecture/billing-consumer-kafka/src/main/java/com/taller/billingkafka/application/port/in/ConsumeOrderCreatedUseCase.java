package com.taller.billingkafka.application.port.in;

import com.taller.billingkafka.domain.model.OrderCreatedEvent;

public interface ConsumeOrderCreatedUseCase {
    void consume(OrderCreatedEvent event);
}
