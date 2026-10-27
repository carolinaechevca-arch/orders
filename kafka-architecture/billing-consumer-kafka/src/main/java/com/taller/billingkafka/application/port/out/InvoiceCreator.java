package com.taller.billingkafka.application.port.out;

import com.taller.billingkafka.domain.model.OrderCreatedEvent;

public interface InvoiceCreator {
    void create(OrderCreatedEvent event);
}
