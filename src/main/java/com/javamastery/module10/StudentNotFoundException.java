package com.javamastery.module10;

/**
 * Thrown when a student lookup fails because the ID does not exist.
 *
 * @author Rachman Walker
 * @version 1.0
 * @since Module 10
 */
public class StudentNotFoundException extends RuntimeException {

    /** The student ID that could not be found */
    private final String studentId;

    /**
     * Constructs the exception for a missing student ID.
     * Builds a descriptive message automatically.
     *
     * @param studentId the ID that was not found
     */
    public StudentNotFoundException(String studentId) {
        super("Student not found: " + studentId);
        this.studentId = studentId;
    }

    /**
     * Returns the student ID that could not be found.
     *
     * @return the missing student ID
     */
    public String getStudentId() {
        return studentId;
    }
}