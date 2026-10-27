package com.taller.ordersrabbitmq.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ProcessOrderMessage(
        UUID messageId,
        String messageType,
        UUID correlationId,
        String orderId,
        String customerName,
        String product,
        int quantity,
        Instant createdAt) {
}
