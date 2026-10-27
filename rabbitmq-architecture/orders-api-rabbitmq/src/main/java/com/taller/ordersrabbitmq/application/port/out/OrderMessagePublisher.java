package com.taller.ordersrabbitmq.application.port.out;

import com.taller.ordersrabbitmq.domain.model.ProcessOrderMessage;

public interface OrderMessagePublisher {

    void publish(ProcessOrderMessage message);
}
