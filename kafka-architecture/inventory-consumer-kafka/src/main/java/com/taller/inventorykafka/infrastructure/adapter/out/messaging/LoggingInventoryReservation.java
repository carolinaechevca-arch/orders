package com.taller.inventorykafka.infrastructure.adapter.out.messaging;

import com.taller.inventorykafka.application.port.out.InventoryReservation;
import com.taller.inventorykafka.domain.model.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingInventoryReservation implements InventoryReservation {
    private static final Logger log = LoggerFactory.getLogger(LoggingInventoryReservation.class);

    @Override
    public void reserve(OrderCreatedEvent event) {
        log.info("Reserva de inventario simulada. orderId={}, correlationId={}, eventId={}, product={}, quantity={}", event.data().orderId(), event.correlationId(), event.eventId(), event.data().product(), event.data().quantity());
    }
}
