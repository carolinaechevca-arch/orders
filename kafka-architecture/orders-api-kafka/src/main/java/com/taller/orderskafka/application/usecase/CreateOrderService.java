package com.taller.orderskafka.application.usecase;

import com.taller.orderskafka.application.port.in.CreateOrderUseCase;
import com.taller.orderskafka.application.port.out.OrderEventPublisher;
import com.taller.orderskafka.domain.model.Order;
import com.taller.orderskafka.domain.model.OrderCreatedEvent;
import java.time.Clock;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@RequiredArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateOrderService.class);
    private final OrderEventPublisher eventPublisher;
    private final Clock clock;

    @Override
    public AcceptedOrder createOrder(CreateOrderCommand command) {
        String orderId = "ORD-" + UUID.randomUUID().toString().toUpperCase();
        UUID eventId = UUID.randomUUID();
        UUID correlationId = UUID.randomUUID();
        MDC.put("orderId", orderId);
        MDC.put("correlationId", correlationId.toString());
        try {
            log.info("Entrando al caso de uso de creación de pedido. orderId={}, correlationId={}, eventId={}", orderId, correlationId, eventId);
            Order order = new Order(
                    orderId, command.customerName(), command.product(), command.quantity(), command.unitPrice(),
                    command.unitPrice().multiply(java.math.BigDecimal.valueOf(command.quantity())));
            log.info("Dominio de pedido creado. orderId={}, correlationId={}, eventId={}", orderId, correlationId, eventId);
            OrderCreatedEvent event = new OrderCreatedEvent(
                    eventId, "OrderCreated", "1.0", clock.instant(), "orders-api-kafka", correlationId, order);
            eventPublisher.publish(event);
            log.info("Saliendo correctamente del caso de uso. orderId={}, correlationId={}, eventId={}", orderId, correlationId, eventId);
            return new AcceptedOrder(orderId, correlationId);
        } finally {
            MDC.remove("orderId");
            MDC.remove("correlationId");
        }
    }
}
