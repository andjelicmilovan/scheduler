package com.clicker.scheduler.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    @Value("${assembler.rabbitmq.request.queue:media.assembler.requests}")
    private String assemblerRequestsQueueName;

    @Value("${assembler.rabbitmq.results.queue:media.assembler.results}")
    private String assemblerResultsQueueName;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    DirectExchange assemblerResultsExchange() {
        return new DirectExchange(assemblerResultsQueueName);
    }

    @Bean
    Queue queueAssemblerResults() {
        return QueueBuilder.durable(assemblerResultsQueueName).build();
    }

    public String getAssemblerRequestsQueueName() {
        return assemblerRequestsQueueName;
    }

    public String getAssemblerResultsQueueName() {
        return assemblerResultsQueueName;
    }
}
