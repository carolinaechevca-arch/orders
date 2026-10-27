package com.taller.ordersrabbitmq.infrastructure.adapter.out.messaging;

import com.taller.ordersrabbitmq.application.port.out.OrderMessagePublisher;
import com.taller.ordersrabbitmq.domain.exception.OrderPublicationException;
import com.taller.ordersrabbitmq.domain.model.ProcessOrderMessage;
import com.taller.ordersrabbitmq.infrastructure.config.RabbitMqProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMqOrderMessagePublisher implements OrderMessagePublisher {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqOrderMessagePublisher.class);
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqProperties properties;

    @Override
    public void publish(ProcessOrderMessage message) {
        log.info("Entrando al puerto de salida de RabbitMQ. orderId={}, correlationId={}, messageId={}", message.orderId(), message.correlationId(), message.messageId());
        try {
            rabbitTemplate.convertAndSend(properties.exchange(), properties.routingKey(), message, amqpMessage -> {
                amqpMessage.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                amqpMessage.getMessageProperties().setMessageId(message.messageId().toString());
                amqpMessage.getMessageProperties().setCorrelationId(message.correlationId().toString());
                amqpMessage.getMessageProperties().setType(message.messageType());
                return amqpMessage;
            });
            log.info("Mensaje publicado en RabbitMQ. orderId={}, correlationId={}, messageId={}", message.orderId(), message.correlationId(), message.messageId());
        } catch (RuntimeException exception) {
            log.error("Error publicando mensaje en RabbitMQ. orderId={}, correlationId={}, messageId={}", message.orderId(), message.correlationId(), message.messageId(), exception);
            throw new OrderPublicationException("No fue posible publicar el pedido", exception);
        }
    }
}
