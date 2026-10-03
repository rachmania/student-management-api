package com.studentmanagement.config;

import com.javamastery.module10.Course;
import com.javamastery.module10.Student;
import com.javamastery.module10.StudentManagementService;
import com.javamastery.module10.StudentNotFoundException;
import com.javamastery.module10.persistence.StudentJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.NoSuchElementException;

@Configuration
public class DomainServiceConfig {

    @Bean
    public StudentManagementService studentManagementService(StudentJpaRepository studentRepository) {
        return new StudentManagementService(studentRepository);
    }

    @Bean
    CommandLineRunner seedDemoData(StudentManagementService studentManagementService) {
        return args -> {
            seedStudentIfAbsent(studentManagementService, new Student("S001", "Rachmania", "rachmania@hardknox.edu", 3.95));
            seedStudentIfAbsent(studentManagementService, new Student("S002", "Alice", "alice.maitland@hardknox.edu", 3.2));
            seedCourseIfAbsent(studentManagementService, new Course("CS230", "Advanced Java Programming", 3, 2));
        };
    }

    private void seedStudentIfAbsent(StudentManagementService service, Student student) {
        try {
            service.getStudent(student.id());
        } catch (StudentNotFoundException e) {
            service.addStudent(student);
        }
    }

    private void seedCourseIfAbsent(StudentManagementService service, Course course) {
        try {
            service.getCourse(course.code());
        } catch (NoSuchElementException e) {
            service.addCourse(course);
        }
    }
}