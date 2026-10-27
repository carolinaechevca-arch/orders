package com.taller.orderskafka.infrastructure.adapter.in.rest;

import java.util.UUID;

public record CreateOrderResponse(String status, String message, String orderId, UUID correlationId) {
}
