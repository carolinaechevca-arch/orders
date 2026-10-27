package com.taller.inventorykafka.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(UUID eventId, String eventType, String eventVersion, Instant occurredAt, String source, UUID correlationId, OrderData data) {
    public record OrderData(String orderId, String customerName, String product, int quantity, BigDecimal unitPrice, BigDecimal total) {
    }
}
