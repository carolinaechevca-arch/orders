package com.taller.orderskafka.domain.model;

import java.math.BigDecimal;

public record Order(
        String orderId,
        String customerName,
        String product,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal total) {
}
