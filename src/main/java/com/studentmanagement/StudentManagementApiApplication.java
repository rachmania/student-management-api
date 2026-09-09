package com.studentmanagement;

import com.studentmanagement.config.StudentApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StudentApiProperties.class)
public class StudentManagementApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentManagementApiApplication.class, args);
	}

}
