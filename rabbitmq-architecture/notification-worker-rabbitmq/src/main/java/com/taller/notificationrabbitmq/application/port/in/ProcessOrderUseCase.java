package com.taller.notificationrabbitmq.application.port.in;

import com.taller.notificationrabbitmq.domain.model.ProcessOrder;

public interface ProcessOrderUseCase {

    void process(ProcessOrder order);
}
