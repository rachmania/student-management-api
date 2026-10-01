package com.studentmanagement.service;

import com.javamastery.module10.Student;
import com.javamastery.module10.StudentManagementService;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentQueryService {

    private final StudentManagementService studentManagementService;

    public StudentQueryService(StudentManagementService studentManagementService) {
        this.studentManagementService = studentManagementService;
    }

    // nameFilter is genuinely optional — pass null to get every student back
    public List<Student> search(@Nullable String nameFilter) {
        List<Student> all = studentManagementService.getAllStudents();
        if (nameFilter == null) {
            return all;
        }
        return all.stream()
                .filter(s -> s.name().toLowerCase().contains(nameFilter.toLowerCase()))
                .toList();
    }

    // findByExactName goes here, as a second method in the same class:
    public Optional<Student> findByExactName(String name) {
        return studentManagementService.getAllStudents().stream()
                .filter(s -> s.name().equalsIgnoreCase(name))
                .findFirst(); // caller must handle the empty case
    }
}
