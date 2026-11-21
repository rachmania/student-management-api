package com.studentmanagement.messaging;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {

    public static final String ENROLLMENT_QUEUE = "student.enrollment";

    @Bean
    public Queue enrollmentQueue() {
        return new Queue(ENROLLMENT_QUEUE, true); // durable: survives broker restart
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}