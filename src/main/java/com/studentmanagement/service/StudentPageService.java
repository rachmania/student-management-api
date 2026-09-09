package com.studentmanagement.service;

import com.javamastery.module10.Student;
import com.javamastery.module10.StudentManagementService;
import com.studentmanagement.config.StudentApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentPageService {

    private final StudentManagementService studentManagementService;
    private final StudentApiProperties properties;

    public List<Student> findPage(int page) {
        int size = properties.defaultPageSize();
        return studentManagementService.getAllStudents().stream()
                .sorted(Comparator.comparing(Student::name)) // defaultSortField = "name"
                .skip((long) page * size)
                .limit(size)
                .toList();
    }
}
