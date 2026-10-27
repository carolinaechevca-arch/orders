package com.taller.inventorykafka.application.port.out;

import com.taller.inventorykafka.domain.model.OrderCreatedEvent;

public interface InventoryReservation {
    void reserve(OrderCreatedEvent event);
}
