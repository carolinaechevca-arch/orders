package com.taller.orderskafka.infrastructure.adapter.in.rest;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotBlank String customerName,
        @NotBlank String product,
        @NotNull @Positive Integer quantity,
        @NotNull @DecimalMin(value = "0.01") BigDecimal unitPrice) {
}
