package com.studentmanagement.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentNotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentNotificationListener.class);

    @RabbitListener(queues = MessagingConfig.ENROLLMENT_QUEUE)
    public void onStudentEnrolled(StudentEnrolledEvent event) {
        logger.info("Notify: {} enrolled in {}", event.studentName(), event.courseCode());
    }
}
