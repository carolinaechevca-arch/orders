package com.taller.notificationrabbitmq.infrastructure.adapter.in.messaging;

import com.rabbitmq.client.Channel;
import com.taller.notificationrabbitmq.application.port.in.ProcessOrderUseCase;
import com.taller.notificationrabbitmq.domain.model.ProcessOrder;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMqOrderListener {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqOrderListener.class);
    private final ProcessOrderUseCase processOrderUseCase;

    @RabbitListener(id = "notification-process-order", queues = "${app.rabbitmq.queue}", containerFactory = "manualAckListenerContainerFactory")
    public void receive(ProcessOrderMessage message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        MDC.put("orderId", message.orderId());
        MDC.put("correlationId", message.correlationId().toString());
        try {
            log.info("Mensaje recibido desde RabbitMQ. orderId={}, correlationId={}, messageId={}, deliveryTag={}", message.orderId(), message.correlationId(), message.messageId(), deliveryTag);
            log.info("Iniciando procesamiento del mensaje. orderId={}, correlationId={}, messageId={}", message.orderId(), message.correlationId(), message.messageId());
            processOrderUseCase.process(toDomain(message));
            channel.basicAck(deliveryTag, false);
            log.info("Mensaje confirmado manualmente. orderId={}, correlationId={}, messageId={}, deliveryTag={}", message.orderId(), message.correlationId(), message.messageId(), deliveryTag);
            log.info("Procesamiento finalizado correctamente. orderId={}, correlationId={}, messageId={}", message.orderId(), message.correlationId(), message.messageId());
        } catch (RuntimeException exception) {
            log.error("Error procesando mensaje; se reintentará. orderId={}, correlationId={}, messageId={}, deliveryTag={}", message.orderId(), message.correlationId(), message.messageId(), deliveryTag, exception);
            channel.basicNack(deliveryTag, false, true);
        } finally {
            MDC.remove("orderId");
            MDC.remove("correlationId");
        }
    }

    private ProcessOrder toDomain(ProcessOrderMessage message) {
        return new ProcessOrder(
                message.messageId(), message.messageType(), message.correlationId(), message.orderId(),
                message.customerName(), message.product(), message.quantity(), message.createdAt());
    }
}
