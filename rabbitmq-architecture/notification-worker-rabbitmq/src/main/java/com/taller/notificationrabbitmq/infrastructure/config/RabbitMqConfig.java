package com.taller.notificationrabbitmq.infrastructure.config;

import com.taller.notificationrabbitmq.infrastructure.adapter.in.messaging.ProcessOrderMessage;
import java.util.Map;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RabbitMqProperties.class)
public class RabbitMqConfig {

    @Bean
    Queue ordersQueue(RabbitMqProperties properties) {
        return new Queue(properties.queue(), true);
    }

    @Bean
    DirectExchange ordersExchange(RabbitMqProperties properties) {
        return new DirectExchange(properties.exchange(), true, false);
    }

    @Bean
    Binding ordersBinding(Queue ordersQueue, DirectExchange ordersExchange, RabbitMqProperties properties) {
        return BindingBuilder.bind(ordersQueue).to(ordersExchange).with(properties.routingKey());
    }

    @Bean
    MessageConverter messageConverter() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setIdClassMapping(Map.of("ProcessOrder", ProcessOrderMessage.class));
        classMapper.afterPropertiesSet();
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    SimpleRabbitListenerContainerFactory manualAckListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setPrefetchCount(1);
        factory.setDefaultRequeueRejected(true);
        return factory;
    }
}
