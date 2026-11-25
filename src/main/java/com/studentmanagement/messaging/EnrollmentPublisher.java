package com.studentmanagement.messaging;

import com.javamastery.module10.EnrollmentException;
import com.javamastery.module10.Student;
import com.javamastery.module10.StudentManagementService;
import com.studentmanagement.client.EnrollmentNotice;
import com.studentmanagement.client.NotificationServiceClient;
import com.studentmanagement.client.NotificationStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrollmentPublisher {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentPublisher.class);

    private final StudentManagementService studentManagementService;
    private final RabbitTemplate rabbitTemplate;
    private final NotificationServiceClient notificationServiceClient;

    public void enroll(String studentId, String courseCode) throws EnrollmentException {
        studentManagementService.enroll(studentId, courseCode); // throws → nothing below runs
        Student student = studentManagementService.getStudent(studentId);

        // 2. synchronous, declarative HTTP call to the notification service
        try {
            NotificationStatus status = notificationServiceClient.notifyEnrollment(
                    new EnrollmentNotice(student.id(), student.name(), courseCode));
            logger.info("Notification acknowledged: {} ({})", status.id(), status.state());
        } catch (Exception e) {
            logger.warn("Notification call failed (non-fatal): {}", e.getMessage());
        }

        // 3. asynchronous event to RabbitMQ
        rabbitTemplate.convertAndSend(
                MessagingConfig.ENROLLMENT_QUEUE,
                new StudentEnrolledEvent(student.id(), student.name(), courseCode));
    }
}
