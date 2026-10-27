package com.taller.inventorykafka.application.port.in;

import com.taller.inventorykafka.domain.model.OrderCreatedEvent;

public interface ConsumeOrderCreatedUseCase {
    void consume(OrderCreatedEvent event);
}
