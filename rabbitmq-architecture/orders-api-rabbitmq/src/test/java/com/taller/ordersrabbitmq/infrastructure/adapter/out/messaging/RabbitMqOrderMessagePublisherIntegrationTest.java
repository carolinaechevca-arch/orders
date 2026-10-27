package com.taller.ordersrabbitmq.infrastructure.adapter.out.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import com.taller.ordersrabbitmq.application.port.in.CreateOrderUseCase;
import com.taller.ordersrabbitmq.domain.model.ProcessOrderMessage;
import com.taller.ordersrabbitmq.infrastructure.config.RabbitMqProperties;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.rabbitmq.RabbitMQContainer;

@Testcontainers
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class RabbitMqOrderMessagePublisherIntegrationTest {

    @Container
    static final RabbitMQContainer rabbitMq = new RabbitMQContainer("rabbitmq:4.1-management-alpine");

    @DynamicPropertySource
    static void rabbitProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMq::getHost);
        registry.add("spring.rabbitmq.port", rabbitMq::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMq::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMq::getAdminPassword);
    }

    private final CreateOrderUseCase createOrderUseCase;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqProperties properties;

    @Test
    void shouldPublishPersistentMessageWithTraceIdentifiers() {
        CreateOrderUseCase.AcceptedOrder accepted = createOrderUseCase.createOrder(
                new CreateOrderUseCase.CreateOrderCommand("Pedro", "Libro de arquitectura", 1));

        ProcessOrderMessage message = (ProcessOrderMessage) rabbitTemplate.receiveAndConvert(properties.queue(), 5000);

        assertThat(message).isNotNull();
        assertThat(message.messageType()).isEqualTo("ProcessOrder");
        assertThat(message.orderId()).isEqualTo(accepted.orderId());
        assertThat(message.correlationId()).isEqualTo(accepted.correlationId());
        assertThat(message.messageId()).isNotNull();
    }
}
