package com.taller.notificationrabbitmq.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ProcessOrder(
        UUID messageId,
        String messageType,
        UUID correlationId,
        String orderId,
        String customerName,
        String product,
        int quantity,
        Instant createdAt) {
}
