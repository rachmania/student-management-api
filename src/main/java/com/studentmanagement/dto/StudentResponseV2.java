package com.studentmanagement.dto;

import com.javamastery.module10.Student;
import com.javamastery.module10.StudentManagementService;

public record StudentResponseV2(
        String id, String name, String email, double gpa,
        int enrolledCourseCount, boolean honorRoll) {

    private static final double HONOR_ROLL_THRESHOLD = 3.5;

    public static StudentResponseV2 from(Student student, StudentManagementService service) {
        int courseCount = service.getCoursesForStudent(student.id()).size();
        boolean honorRoll = student.gpa() >= HONOR_ROLL_THRESHOLD;
        return new StudentResponseV2(
                student.id(), student.name(), student.email(), student.gpa(),
                courseCount, honorRoll);
    }
}