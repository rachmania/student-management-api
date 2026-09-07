package com.javamastery.module10;

/**
 * Represents a student record with a studentId, name, email and gpa.
 * Demonstrates a record with compact constructor validation.
 *
 * @param id    the student's unique identifier
 * @param name  the student's full name
 * @param email the student's email address
 * @param gpa   the student's grade point average (0.0–4.0)
 *
 * @author Rachman Walker
 * @version 1.0
 * @since Module 10
 */
public record Student(String id, String name, String email, double gpa) {
    /**
     * Compact constructor — validates name is not empty, gpa between 0.0 and 4.0
     */
    public Student{
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if(gpa < 0.0 || gpa > 4.0) {
            throw new IllegalArgumentException("GPA must be between 0.0 and 4.0: " + gpa);
        }
    }
}
