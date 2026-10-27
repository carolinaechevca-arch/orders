package com.taller.notificationkafka.application.port.out;

import com.taller.notificationkafka.domain.model.OrderCreatedEvent;

public interface NotificationSender {
    void send(OrderCreatedEvent event);
}
