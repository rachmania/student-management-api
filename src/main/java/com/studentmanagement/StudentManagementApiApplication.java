package com.studentmanagement;

import com.studentmanagement.config.StudentApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableConfigurationProperties(StudentApiProperties.class)
@EnableJpaRepositories(basePackages = "com.javamastery.module10.persistence")
@EntityScan(basePackages = "com.javamastery.module10.persistence")
public class StudentManagementApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(StudentManagementApiApplication.class, args);
	}
}
