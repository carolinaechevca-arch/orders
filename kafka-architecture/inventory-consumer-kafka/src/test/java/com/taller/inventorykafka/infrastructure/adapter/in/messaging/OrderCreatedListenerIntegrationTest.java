package com.taller.inventorykafka.infrastructure.adapter.in.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import com.taller.inventorykafka.application.port.out.InventoryReservation;
import com.taller.inventorykafka.domain.model.OrderCreatedEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

@Testcontainers
@SpringBootTest(classes = {com.taller.inventorykafka.Application.class, OrderCreatedListenerIntegrationTest.RecordingConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class OrderCreatedListenerIntegrationTest {
    @Container
    static final KafkaContainer kafka = new KafkaContainer("apache/kafka-native:4.1.0");

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.producer.key-serializer", () -> "org.apache.kafka.common.serialization.StringSerializer");
        registry.add("spring.kafka.producer.value-serializer", () -> "org.springframework.kafka.support.serializer.JsonSerializer");
        registry.add("spring.kafka.producer.properties.spring.json.add.type.headers", () -> false);
    }

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final BlockingQueue<OrderCreatedEvent> processedEvents;
    private final KafkaListenerEndpointRegistry listenerRegistry;

    @Test
    void shouldConsumeEventAndPropagateTraceIdentifiers() throws Exception {
        UUID eventId = UUID.randomUUID();
        UUID correlationId = UUID.randomUUID();
        OrderCreatedEvent event = new OrderCreatedEvent(
                eventId, "OrderCreated", "1.0", Instant.now(), "orders-api-kafka", correlationId,
                new OrderCreatedEvent.OrderData("ORD-123", "Pedro", "Libro", 1, new BigDecimal("85000"), new BigDecimal("85000")));
        kafkaTemplate.send("orders.events", event.data().orderId(), event).get(10, TimeUnit.SECONDS);
        OrderCreatedEvent processed = processedEvents.poll(10, TimeUnit.SECONDS);
        assertThat(processed).isNotNull();
        assertThat(processed.eventId()).isEqualTo(eventId);
        assertThat(processed.correlationId()).isEqualTo(correlationId);
        assertThat(processed.data().orderId()).isEqualTo("ORD-123");
    }

    @Test
    void shouldRecoverEventsPublishedWhileConsumerIsStopped() throws Exception {
        var listenerContainer = listenerRegistry.getListenerContainer("inventory-order-created");
        CountDownLatch stopped = new CountDownLatch(1);
        listenerContainer.stop(stopped::countDown);
        assertThat(stopped.await(10, TimeUnit.SECONDS)).isTrue();
        processedEvents.clear();
        OrderCreatedEvent event = event("ORD-RECOVERY");
        kafkaTemplate.send("orders.events", event.data().orderId(), event).get(10, TimeUnit.SECONDS);
        assertThat(processedEvents.poll(1, TimeUnit.SECONDS)).isNull();
        listenerContainer.start();
        OrderCreatedEvent recovered = processedEvents.poll(10, TimeUnit.SECONDS);
        assertThat(recovered).isNotNull();
        assertThat(recovered.data().orderId()).isEqualTo("ORD-RECOVERY");
    }

    private OrderCreatedEvent event(String orderId) {
        return new OrderCreatedEvent(
                UUID.randomUUID(), "OrderCreated", "1.0", Instant.now(), "orders-api-kafka", UUID.randomUUID(),
                new OrderCreatedEvent.OrderData(orderId, "Pedro", "Libro", 1, new BigDecimal("85000"), new BigDecimal("85000")));
    }

    @TestConfiguration
    static class RecordingConfig {
        @Bean
        BlockingQueue<OrderCreatedEvent> processedEvents() {
            return new LinkedBlockingQueue<>();
        }

        @Bean
        @Primary
        InventoryReservation recordingInventoryReservation(BlockingQueue<OrderCreatedEvent> processedEvents) {
            return processedEvents::add;
        }
    }
}
