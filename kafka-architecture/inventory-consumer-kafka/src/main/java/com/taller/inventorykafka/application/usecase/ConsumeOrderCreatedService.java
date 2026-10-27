package com.taller.inventorykafka.application.usecase;

import com.taller.inventorykafka.application.port.in.ConsumeOrderCreatedUseCase;
import com.taller.inventorykafka.application.port.out.InventoryReservation;
import com.taller.inventorykafka.domain.model.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class ConsumeOrderCreatedService implements ConsumeOrderCreatedUseCase {
    private static final Logger log = LoggerFactory.getLogger(ConsumeOrderCreatedService.class);
    private final InventoryReservation inventoryReservation;

    @Override
    public void consume(OrderCreatedEvent event) {
        log.info("Entrando al caso de uso de inventario. orderId={}, correlationId={}, eventId={}", event.data().orderId(), event.correlationId(), event.eventId());
        inventoryReservation.reserve(event);
        log.info("Saliendo correctamente del caso de uso de inventario. orderId={}, correlationId={}, eventId={}", event.data().orderId(), event.correlationId(), event.eventId());
    }
}
