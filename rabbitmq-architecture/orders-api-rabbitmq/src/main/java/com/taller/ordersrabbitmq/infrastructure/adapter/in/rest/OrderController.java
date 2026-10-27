package com.taller.ordersrabbitmq.infrastructure.adapter.in.rest;

import com.taller.ordersrabbitmq.application.port.in.CreateOrderUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final CreateOrderUseCase createOrderUseCase;

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Solicitud recibida en POST /orders. customerName={}, product={}", request.customerName(), request.product());
        log.info("Solicitud de pedido validada. customerName={}, product={}, quantity={}", request.customerName(), request.product(), request.quantity());
        CreateOrderUseCase.AcceptedOrder acceptedOrder = createOrderUseCase.createOrder(
                new CreateOrderUseCase.CreateOrderCommand(request.customerName(), request.product(), request.quantity()));
        CreateOrderResponse response = new CreateOrderResponse(
                "accepted",
                "Pedido recibido y enviado a RabbitMQ",
                acceptedOrder.orderId(),
                acceptedOrder.correlationId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
