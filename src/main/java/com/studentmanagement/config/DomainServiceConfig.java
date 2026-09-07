package com.studentmanagement.config;

import com.javamastery.module10.Course;
import com.javamastery.module10.Student;
import com.javamastery.module10.StudentManagementService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public StudentManagementService studentManagementService() {
        return new StudentManagementService();
    }

    @Bean
    CommandLineRunner seedDemoData(StudentManagementService studentManagementService) {
        return args -> {
            studentManagementService.addStudent(new Student("S001", "Rachmania", "rachmania@hardknox.edu", 3.9));
            studentManagementService.addStudent(new Student("S002", "Alice", "alice.maitland@hardknox.edu", 3.2));
            studentManagementService.addCourse(new Course("CS230", "Advanced Java Programming", 3, 2));
        };
    }
}
