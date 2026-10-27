package com.taller.billingkafka.application.usecase;

import com.taller.billingkafka.application.port.in.ConsumeOrderCreatedUseCase;
import com.taller.billingkafka.application.port.out.InvoiceCreator;
import com.taller.billingkafka.domain.model.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class ConsumeOrderCreatedService implements ConsumeOrderCreatedUseCase {
    private static final Logger log = LoggerFactory.getLogger(ConsumeOrderCreatedService.class);
    private final InvoiceCreator invoiceCreator;

    @Override
    public void consume(OrderCreatedEvent event) {
        log.info("Entrando al caso de uso de facturación. orderId={}, correlationId={}, eventId={}", event.data().orderId(), event.correlationId(), event.eventId());
        invoiceCreator.create(event);
        log.info("Saliendo correctamente del caso de uso de facturación. orderId={}, correlationId={}, eventId={}", event.data().orderId(), event.correlationId(), event.eventId());
    }
}
