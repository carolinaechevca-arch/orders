package com.taller.orderskafka.infrastructure.adapter.out.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import com.taller.orderskafka.application.port.in.CreateOrderUseCase;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

@Testcontainers
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class KafkaOrderEventPublisherIntegrationTest {
    @Container
    static final KafkaContainer kafka = new KafkaContainer("apache/kafka-native:4.1.0");

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    private final CreateOrderUseCase createOrderUseCase;

    @Test
    void shouldPublishOrderCreatedUsingOrderIdAsKey() {
        Map<String, Object> properties = KafkaTestUtils.consumerProps(kafka.getBootstrapServers(), "test-" + UUID.randomUUID(), "true");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        try (var consumer = new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(), new StringDeserializer()).createConsumer()) {
            consumer.subscribe(java.util.List.of("orders.events"));
            CreateOrderUseCase.AcceptedOrder accepted = createOrderUseCase.createOrder(
                    new CreateOrderUseCase.CreateOrderCommand("Pedro", "Libro", 1, new java.math.BigDecimal("85000")));
            ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, "orders.events", Duration.ofSeconds(10));
            assertThat(record.key()).isEqualTo(accepted.orderId());
            assertThat(record.value()).contains("\"eventType\":\"OrderCreated\"");
            assertThat(record.value()).contains(accepted.orderId());
            assertThat(record.value()).contains(accepted.correlationId().toString());
        }
    }
}
