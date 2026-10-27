package com.taller.orderskafka.domain.model;

import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        String eventType,
        String eventVersion,
        Instant occurredAt,
        String source,
        UUID correlationId,
        Order data) {
}
