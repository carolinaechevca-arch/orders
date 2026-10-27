package com.taller.notificationkafka.application.usecase;

import com.taller.notificationkafka.application.port.in.ConsumeOrderCreatedUseCase;
import com.taller.notificationkafka.application.port.out.NotificationSender;
import com.taller.notificationkafka.domain.model.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class ConsumeOrderCreatedService implements ConsumeOrderCreatedUseCase {
    private static final Logger log = LoggerFactory.getLogger(ConsumeOrderCreatedService.class);
    private final NotificationSender notificationSender;

    @Override
    public void consume(OrderCreatedEvent event) {
        log.info("Entrando al caso de uso de notificación. orderId={}, correlationId={}, eventId={}", event.data().orderId(), event.correlationId(), event.eventId());
        notificationSender.send(event);
        log.info("Saliendo correctamente del caso de uso de notificación. orderId={}, correlationId={}, eventId={}", event.data().orderId(), event.correlationId(), event.eventId());
    }
}
