package com.studentmanagement.controller;

import com.javamastery.module10.EnrollmentException;
import com.javamastery.module10.Student;
import com.javamastery.module10.StudentManagementService;
import com.studentmanagement.dto.StudentRequest;
import com.studentmanagement.dto.StudentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentManagementService studentManagementService;

    @GetMapping
    public List<StudentResponse> getAll() {
        return studentManagementService.getAllStudents().stream()
                .map(StudentResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public StudentResponse getOne(@PathVariable String id) {
        return StudentResponse.from(studentManagementService.getStudent(id));
    }

    @PostMapping
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request) {
        Student student = new Student(request.id(), request.name(), request.email(), request.gpa());
        studentManagementService.addStudent(student);
        return ResponseEntity
                .created(URI.create("/api/students/" + student.id()))
                .body(StudentResponse.from(student));
    }

    @PostMapping("/{studentId}/enroll/{courseCode}")
    public ResponseEntity<Void> enroll(@PathVariable String studentId,
                                       @PathVariable String courseCode) throws EnrollmentException {
        studentManagementService.enroll(studentId, courseCode);
        return ResponseEntity.ok().build();
    }
}