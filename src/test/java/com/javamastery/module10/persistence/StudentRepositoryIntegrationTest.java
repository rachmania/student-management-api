package com.javamastery.module10.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;   // Testcontainers 2.0 package

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import com.studentmanagement.StudentManagementApiApplication;
import org.springframework.test.context.ContextConfiguration;

@Testcontainers
@DataJpaTest
@ContextConfiguration(classes = StudentManagementApiApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class StudentRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");  // NO <?> — non-generic in TC 2.0

    @Autowired
    private StudentJpaRepository studentRepository;

    @Test
    void savesAndRetrievesStudent_againstRealPostgres() {
        StudentEntity entity = new StudentEntity("S500", "Grace", "grace@uni.edu", 3.8);
        studentRepository.save(entity);

        Optional<StudentEntity> found = studentRepository.findById("S500");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Grace");
        assertThat(found.get().getEmail()).isEqualTo("grace@uni.edu");
    }

    @Test
    void existsById_returnsFalse_forUnknownStudent() {
        assertThat(studentRepository.existsById("S999")).isFalse();
    }
}
