package com.taller.orderskafka.infrastructure.adapter.in.rest;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ApiErrorResponse(Instant timestamp, int status, String error, String message, String path, UUID correlationId, Map<String, String> validationErrors) {
}
