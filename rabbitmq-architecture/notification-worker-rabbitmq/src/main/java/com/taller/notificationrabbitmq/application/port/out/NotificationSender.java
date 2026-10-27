package com.taller.notificationrabbitmq.application.port.out;

import com.taller.notificationrabbitmq.domain.model.ProcessOrder;

public interface NotificationSender {

    void send(ProcessOrder order);
}
