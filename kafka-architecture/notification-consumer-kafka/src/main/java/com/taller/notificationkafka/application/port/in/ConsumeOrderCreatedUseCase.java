package com.taller.notificationkafka.application.port.in;

import com.taller.notificationkafka.domain.model.OrderCreatedEvent;

public interface ConsumeOrderCreatedUseCase {
    void consume(OrderCreatedEvent event);
}
