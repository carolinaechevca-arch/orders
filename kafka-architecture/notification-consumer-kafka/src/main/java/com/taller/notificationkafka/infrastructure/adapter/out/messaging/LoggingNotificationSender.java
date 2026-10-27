package com.taller.notificationkafka.infrastructure.adapter.out.messaging;

import com.taller.notificationkafka.application.port.out.NotificationSender;
import com.taller.notificationkafka.domain.model.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingNotificationSender implements NotificationSender {
    private static final Logger log = LoggerFactory.getLogger(LoggingNotificationSender.class);

    @Override
    public void send(OrderCreatedEvent event) {
        log.info("Notificación simulada enviada. orderId={}, correlationId={}, eventId={}, customerName={}", event.data().orderId(), event.correlationId(), event.eventId(), event.data().customerName());
    }
}
