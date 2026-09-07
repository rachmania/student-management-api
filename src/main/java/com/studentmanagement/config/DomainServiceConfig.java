package com.studentmanagement.config;

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
}
