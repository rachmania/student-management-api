package com.javamastery.module10;

/**
 * Represents a course with a course, title, credits and capacity.
 * Demonstrates a record with compact constructor validation.
 *
 * @param code      the course name (e.g. "CS101")
 * @param title     the course title
 * @param credits   the number of credits (1-6)
 * @param capacity  the maximum enrollment capacity (positive)
 *
 * @author Rachman Walker
 * @version 1.0
 * @since Module 10
 */
public record Course(String code, String title, int credits, int capacity) {
    /**
     * Compact constructor — validates code and title are not blank,
     * credits are 1–6, and capacity is positive.
     *
     * @throws IllegalArgumentException if any field is invalid
     */
    public Course {
        if(code == null || code.isBlank())
            throw new IllegalArgumentException("Course code cannot be empty");
        if(title == null || title.isBlank())
            throw new IllegalArgumentException("Course title cannot be empty");
        if(credits < 1 || credits > 6)
            throw new IllegalArgumentException("Course credits must be between 1 and 6: " + credits);
        if(capacity < 1)
            throw new IllegalArgumentException("Course capacity must be positive: " + capacity);
    }
}
