package com.taller.orderskafka.application.port.out;

import com.taller.orderskafka.domain.model.OrderCreatedEvent;

public interface OrderEventPublisher {

    void publish(OrderCreatedEvent event);
}
