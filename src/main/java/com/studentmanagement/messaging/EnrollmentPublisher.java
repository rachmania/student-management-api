package com.studentmanagement.messaging;

import com.javamastery.module10.EnrollmentException;
import com.javamastery.module10.Student;
import com.javamastery.module10.StudentManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrollmentPublisher {

    private final StudentManagementService studentManagementService;
    private final RabbitTemplate rabbitTemplate;

    public void enroll(String studentId, String courseCode) throws EnrollmentException {
        studentManagementService.enroll(studentId, courseCode); // throws → no event published
        Student student = studentManagementService.getStudent(studentId);
        rabbitTemplate.convertAndSend(
                MessagingConfig.ENROLLMENT_QUEUE,
                new StudentEnrolledEvent(student.id(), student.name(), courseCode));
    }
}