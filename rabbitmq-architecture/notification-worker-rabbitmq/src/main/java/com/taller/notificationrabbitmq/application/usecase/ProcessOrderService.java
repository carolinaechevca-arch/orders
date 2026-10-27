package com.taller.notificationrabbitmq.application.usecase;

import com.taller.notificationrabbitmq.application.port.in.ProcessOrderUseCase;
import com.taller.notificationrabbitmq.application.port.out.NotificationSender;
import com.taller.notificationrabbitmq.domain.model.ProcessOrder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class ProcessOrderService implements ProcessOrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessOrderService.class);
    private final NotificationSender notificationSender;

    @Override
    public void process(ProcessOrder order) {
        log.info("Entrando al caso de uso de notificación. orderId={}, correlationId={}, messageId={}", order.orderId(), order.correlationId(), order.messageId());
        notificationSender.send(order);
        log.info("Saliendo correctamente del caso de uso de notificación. orderId={}, correlationId={}, messageId={}", order.orderId(), order.correlationId(), order.messageId());
    }
}
