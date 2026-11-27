package com.studentmanagement.service;

import com.javamastery.module10.Student;
import com.javamastery.module10.StudentManagementService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstrumentedStudentService {

    private final StudentManagementService studentManagementService;
    private final MeterRegistry meterRegistry;

    public void addStudent(Student student) {
        studentManagementService.addStudent(student);          // throws on duplicate → no increment
        meterRegistry.counter("students.created").increment(); // only successful adds count
    }
}