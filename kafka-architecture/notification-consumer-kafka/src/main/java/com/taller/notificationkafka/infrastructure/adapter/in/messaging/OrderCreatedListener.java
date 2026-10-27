package com.taller.notificationkafka.infrastructure.adapter.in.messaging;

import com.taller.notificationkafka.application.port.in.ConsumeOrderCreatedUseCase;
import com.taller.notificationkafka.domain.model.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCreatedListener {
    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);
    private final ConsumeOrderCreatedUseCase useCase;

    @KafkaListener(topics = "${app.kafka.topic}")
    public void receive(ConsumerRecord<String, OrderCreatedEvent> record) {
        OrderCreatedEvent event = record.value();
        MDC.put("orderId", event.data().orderId());
        MDC.put("correlationId", event.correlationId().toString());
        try {
            log.info("Evento recibido en notificación. orderId={}, correlationId={}, eventId={}, topic={}, partition={}, offset={}", event.data().orderId(), event.correlationId(), event.eventId(), record.topic(), record.partition(), record.offset());
            log.info("Iniciando procesamiento de notificación. orderId={}, correlationId={}, eventId={}", event.data().orderId(), event.correlationId(), event.eventId());
            useCase.consume(event);
            log.info("Procesamiento de notificación finalizado correctamente. orderId={}, correlationId={}, eventId={}", event.data().orderId(), event.correlationId(), event.eventId());
        } catch (RuntimeException exception) {
            log.error("Error procesando notificación; Kafka reintentará el registro. orderId={}, correlationId={}, eventId={}", event.data().orderId(), event.correlationId(), event.eventId(), exception);
            throw exception;
        } finally {
            MDC.clear();
        }
    }
}
