package com.taller.notificationrabbitmq.infrastructure.adapter.out.messaging;

import com.taller.notificationrabbitmq.application.port.out.NotificationSender;
import com.taller.notificationrabbitmq.domain.exception.NotificationProcessingException;
import com.taller.notificationrabbitmq.domain.model.ProcessOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingNotificationSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(LoggingNotificationSender.class);

    @Override
    public void send(ProcessOrder order) {
        log.info("Iniciando envío simulado de notificación. orderId={}, correlationId={}, messageId={}", order.orderId(), order.correlationId(), order.messageId());
        try {
            Thread.sleep(500);
            log.info("Notificación simulada enviada correctamente. orderId={}, correlationId={}, messageId={}", order.orderId(), order.correlationId(), order.messageId());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new NotificationProcessingException("El envío simulado fue interrumpido", exception);
        }
    }
}
