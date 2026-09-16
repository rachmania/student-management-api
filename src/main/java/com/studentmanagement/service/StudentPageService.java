package com.studentmanagement.service;

import com.javamastery.module10.Student;
import com.javamastery.module10.StudentManagementService;
import com.studentmanagement.config.AuditLogger;
import com.studentmanagement.config.StudentApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentPageService {

    private final StudentManagementService studentManagementService;
    private final StudentApiProperties properties;
    private final Optional<AuditLogger> auditLogger;

    public List<Student> findPage(int page) {
        auditLogger.ifPresent(logger -> logger.log("Listed students page " + page));

        int size = properties.defaultPageSize();
        return studentManagementService.getAllStudents().stream()
                .sorted(Comparator.comparing(Student::name))
                .skip((long) page * size)
                .limit(size)
                .toList();
    }
}
