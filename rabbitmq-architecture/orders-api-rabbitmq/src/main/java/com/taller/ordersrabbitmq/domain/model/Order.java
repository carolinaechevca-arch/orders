package com.taller.ordersrabbitmq.domain.model;

import java.time.Instant;

public record Order(
        String orderId,
        String customerName,
        String product,
        int quantity,
        Instant createdAt) {
}
