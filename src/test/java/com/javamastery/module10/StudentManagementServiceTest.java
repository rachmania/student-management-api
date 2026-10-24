package com.javamastery.module10;


import com.javamastery.module10.persistence.StudentEntity;
import com.javamastery.module10.persistence.StudentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentManagementServiceTest {

    @Mock
    private StudentJpaRepository studentRepository;   // the fake repository

    @InjectMocks
    private StudentManagementService service;         // real service, with the mock injected

    private Student alice;

    @BeforeEach
    void setUp() {
        alice = new Student("S001", "Alice", "alice@uni.edu", 3.9);
    }

    @Test
    void getStudent_returnsStudent_whenFound() {
        // given: the repository will return an entity for S001
        when(studentRepository.findById("S001"))
                .thenReturn(Optional.of(new StudentEntity("S001", "Alice", "alice@uni.edu", 3.9)));

        // when
        Student result = service.getStudent("S001");

        // then
        assertThat(result.id()).isEqualTo("S001");
        assertThat(result.name()).isEqualTo("Alice");
    }

    @Test
    void getStudent_throwsNotFound_whenMissing() {
        // given: the repository returns empty for an unknown id
        when(studentRepository.findById("S999")).thenReturn(Optional.empty());

        // when / then: the service translates that into StudentNotFoundException
        assertThatThrownBy(() -> service.getStudent("S999"))
                .isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    void addStudent_throwsConflict_whenDuplicate() {
        // given: the repository reports the id already exists
        when(studentRepository.existsById("S001")).thenReturn(true);

        // when / then: the service rejects the duplicate
        assertThatThrownBy(() -> service.addStudent(alice))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
