package com.taller.ordersrabbitmq.application.port.in;

import java.util.UUID;

public interface CreateOrderUseCase {

    AcceptedOrder createOrder(CreateOrderCommand command);

    record CreateOrderCommand(String customerName, String product, int quantity) {
    }

    record AcceptedOrder(String orderId, UUID correlationId) {
    }
}
