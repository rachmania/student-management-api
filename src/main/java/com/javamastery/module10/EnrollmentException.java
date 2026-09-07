package com.javamastery.module10;

import lombok.Getter;

@Getter
public class EnrollmentException extends Exception {
    private final String courseId;
    private final String userId;
    private final FailureReason reason;

    public enum FailureReason {
        COURSE_FULL,
        ALREADY_ENROLLED,
        UNPAID_ENROLLMENT_FEES,
        PREREQUISITES_NOT_MET,
        SYSTEM_ERROR
    }

    //  Custom constructor to build a clear, concise error message
    public EnrollmentException(String userId, String courseId, FailureReason reason) {
        super("User [%s] failed to enroll in course [%s]. Reason: %s".formatted(userId, courseId, reason));
        this.courseId = courseId;
        this.userId = userId;
        this.reason = reason;
    }
}
