package com.studentmanagement.dto;

public record StudentResponse(String id, String name, String email, double gpa) {

    public static StudentResponse from(com.javamastery.module10.Student student) {
        return new StudentResponse(student.id(), student.name(), student.email(), student.gpa());
    }
}