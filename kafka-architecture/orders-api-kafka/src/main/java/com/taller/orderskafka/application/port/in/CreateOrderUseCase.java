package com.taller.orderskafka.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface CreateOrderUseCase {

    AcceptedOrder createOrder(CreateOrderCommand command);

    record CreateOrderCommand(String customerName, String product, int quantity, BigDecimal unitPrice) {
    }

    record AcceptedOrder(String orderId, UUID correlationId) {
    }
}
