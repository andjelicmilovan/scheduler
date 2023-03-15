package com.clicker.scheduler.service;

import com.c9weather.domain.assembly.VideoAssemblyTaskRequest;
import com.clicker.scheduler.config.RabbitMQConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    static final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final RabbitMQConfiguration rabbitMQConfiguration;
    private final RabbitTemplate rabbitTemplate;

    public MessageService(RabbitMQConfiguration rabbitMQConfiguration, RabbitTemplate rabbitTemplate) {
        this.rabbitMQConfiguration = rabbitMQConfiguration;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessageToVideoAssembly(VideoAssemblyTaskRequest taskRequest) {
        log.info("LATEST VERSION");
        log.debug("Sending video assembly task request {} to queue", taskRequest.getId());
        rabbitTemplate.convertAndSend(rabbitMQConfiguration.getAssemblerRequestsQueueName(), taskRequest);
        log.info("Task request {} successfully sent to video assembly queue", taskRequest.getId());
    }
}
