package com.studentmanagement.messaging;

public record StudentEnrolledEvent(String studentId, String studentName, String courseCode) {
}
