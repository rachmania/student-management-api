package com.studentmanagement.controller;

import com.javamastery.module10.Student;
import com.javamastery.module10.StudentManagementService;
import com.studentmanagement.service.StudentPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentManagementService studentManagementService;
    private final StudentPageService studentPageService;

    @GetMapping
    public List<Student> getAll() {
        return studentManagementService.getAllStudents();
    }

    @GetMapping("/{id}")
    public Student getOne(@PathVariable String id) {
        return studentManagementService.getStudent(id); // still an unhandled 500 for now — Module 7 fixes this
    }

    @GetMapping("/page/{page}")
    public List<Student> getPage(@PathVariable int page) {
        return studentPageService.findPage(page);
    }
}