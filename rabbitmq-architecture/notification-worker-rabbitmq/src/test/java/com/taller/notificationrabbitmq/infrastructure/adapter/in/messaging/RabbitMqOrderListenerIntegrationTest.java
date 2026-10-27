package com.taller.notificationrabbitmq.infrastructure.adapter.in.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import com.taller.notificationrabbitmq.application.port.out.NotificationSender;
import com.taller.notificationrabbitmq.domain.model.ProcessOrder;
import com.taller.notificationrabbitmq.infrastructure.config.RabbitMqProperties;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.rabbitmq.RabbitMQContainer;

@Testcontainers
@SpringBootTest(classes = {com.taller.notificationrabbitmq.Application.class, RabbitMqOrderListenerIntegrationTest.RecordingConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class RabbitMqOrderListenerIntegrationTest {

    @Container
    static final RabbitMQContainer rabbitMq = new RabbitMQContainer("rabbitmq:4.1-management-alpine");

    @DynamicPropertySource
    static void rabbitProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMq::getHost);
        registry.add("spring.rabbitmq.port", rabbitMq::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMq::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMq::getAdminPassword);
    }

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqProperties properties;
    private final BlockingQueue<ProcessOrder> processedOrders;
    private final RabbitListenerEndpointRegistry listenerRegistry;

    @Test
    void shouldConsumeMessageAndPropagateTraceIdentifiers() throws InterruptedException {
        UUID messageId = UUID.randomUUID();
        UUID correlationId = UUID.randomUUID();
        ProcessOrderMessage message = new ProcessOrderMessage(
                messageId, "ProcessOrder", correlationId, "ORD-123", "Pedro", "Libro", 1, Instant.now());

        rabbitTemplate.convertAndSend(properties.exchange(), properties.routingKey(), message);

        ProcessOrder processed = processedOrders.poll(10, TimeUnit.SECONDS);
        assertThat(processed).isNotNull();
        assertThat(processed.orderId()).isEqualTo("ORD-123");
        assertThat(processed.correlationId()).isEqualTo(correlationId);
        assertThat(processed.messageId()).isEqualTo(messageId);
    }

    @Test
    void shouldRecoverMessagePublishedWhileConsumerIsStopped() throws Exception {
        var listenerContainer = listenerRegistry.getListenerContainer("notification-process-order");
        CountDownLatch stopped = new CountDownLatch(1);
        listenerContainer.stop(stopped::countDown);
        assertThat(stopped.await(10, TimeUnit.SECONDS)).isTrue();
        processedOrders.clear();
        ProcessOrderMessage message = message("ORD-RECOVERY");
        rabbitTemplate.convertAndSend(properties.exchange(), properties.routingKey(), message);
        Long queued = rabbitTemplate.execute(channel -> channel.messageCount(properties.queue()));
        assertThat(queued).isEqualTo(1L);
        listenerContainer.start();
        ProcessOrder recovered = processedOrders.poll(10, TimeUnit.SECONDS);
        assertThat(recovered).isNotNull();
        assertThat(recovered.orderId()).isEqualTo("ORD-RECOVERY");
    }

    private ProcessOrderMessage message(String orderId) {
        return new ProcessOrderMessage(
                UUID.randomUUID(), "ProcessOrder", UUID.randomUUID(), orderId, "Pedro", "Libro", 1, Instant.now());
    }

    @TestConfiguration
    static class RecordingConfig {

        @Bean
        BlockingQueue<ProcessOrder> processedOrders() {
            return new LinkedBlockingQueue<>();
        }

        @Bean
        @Primary
        NotificationSender recordingNotificationSender(BlockingQueue<ProcessOrder> processedOrders) {
            return processedOrders::add;
        }
    }
}
