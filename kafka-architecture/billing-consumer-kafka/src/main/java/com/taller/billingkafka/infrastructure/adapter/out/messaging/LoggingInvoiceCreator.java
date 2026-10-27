package com.taller.billingkafka.infrastructure.adapter.out.messaging;

import com.taller.billingkafka.application.port.out.InvoiceCreator;
import com.taller.billingkafka.domain.model.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingInvoiceCreator implements InvoiceCreator {
    private static final Logger log = LoggerFactory.getLogger(LoggingInvoiceCreator.class);

    @Override
    public void create(OrderCreatedEvent event) {
        log.info("Factura simulada creada. orderId={}, correlationId={}, eventId={}, total={}", event.data().orderId(), event.correlationId(), event.eventId(), event.data().total());
    }
}
