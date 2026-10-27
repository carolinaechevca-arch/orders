package com.taller.orderskafka.infrastructure.adapter.out.messaging;

import com.taller.orderskafka.application.port.out.OrderEventPublisher;
import com.taller.orderskafka.domain.model.OrderCreatedEvent;
import com.taller.orderskafka.infrastructure.config.KafkaProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaOrderEventPublisher implements OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaOrderEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties properties;

    @Override
    public void publish(OrderCreatedEvent event) {
        log.info("Entrando al puerto de salida de Kafka. orderId={}, correlationId={}, eventId={}", event.data().orderId(), event.correlationId(), event.eventId());
        kafkaTemplate.send(properties.topic(), event.data().orderId(), event).whenComplete((result, exception) -> {
            if (exception == null) {
                log.info(
                        "Evento publicado en Kafka. orderId={}, correlationId={}, eventId={}, topic={}, partition={}, offset={}",
                        event.data().orderId(), event.correlationId(), event.eventId(), result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            } else {
                log.error("Error publicando evento en Kafka. orderId={}, correlationId={}, eventId={}", event.data().orderId(), event.correlationId(), event.eventId(), exception);
            }
        });
    }
}
