package com.taller.ordersrabbitmq.application.usecase;

import com.taller.ordersrabbitmq.application.port.in.CreateOrderUseCase;
import com.taller.ordersrabbitmq.application.port.out.OrderMessagePublisher;
import com.taller.ordersrabbitmq.domain.model.Order;
import com.taller.ordersrabbitmq.domain.model.ProcessOrderMessage;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@RequiredArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateOrderService.class);
    private final OrderMessagePublisher messagePublisher;
    private final Clock clock;

    @Override
    public AcceptedOrder createOrder(CreateOrderCommand command) {
        String orderId = "ORD-" + UUID.randomUUID().toString().toUpperCase();
        UUID correlationId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        MDC.put("orderId", orderId);
        MDC.put("correlationId", correlationId.toString());
        try {
            log.info("Entrando al caso de uso de creación de pedido. orderId={}, correlationId={}", orderId, correlationId);
            Instant createdAt = clock.instant();
            Order order = new Order(orderId, command.customerName(), command.product(), command.quantity(), createdAt);
            log.info("Dominio de pedido creado. orderId={}, correlationId={}", orderId, correlationId);
            ProcessOrderMessage message = new ProcessOrderMessage(
                    messageId,
                    "ProcessOrder",
                    correlationId,
                    order.orderId(),
                    order.customerName(),
                    order.product(),
                    order.quantity(),
                    order.createdAt());
            messagePublisher.publish(message);
            log.info("Saliendo correctamente del caso de uso. orderId={}, correlationId={}, messageId={}", orderId, correlationId, messageId);
            return new AcceptedOrder(orderId, correlationId);
        } finally {
            MDC.remove("orderId");
            MDC.remove("correlationId");
        }
    }
}
