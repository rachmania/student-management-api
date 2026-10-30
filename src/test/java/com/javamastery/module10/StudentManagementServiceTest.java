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
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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

    // ---------- Enrollment business rules ----------

    @Test
    void enroll_throwsCourseFull_whenCapacityReached() {
        // given: a course with capacity 1, and two students who exist
        when(studentRepository.findById("S001"))
                .thenReturn(Optional.of(new StudentEntity("S001", "Alice", "alice@uni.edu", 3.9)));
        when(studentRepository.findById("S002"))
                .thenReturn(Optional.of(new StudentEntity("S002", "Bob", "bob@uni.edu", 3.2)));

        service.addCourse(new Course("CS101", "Intro", 3, 1));   // capacity = 1
        assertDoesNotThrow(() -> service.enroll("S001", "CS101")); // fills the one seat

        // when/then: the second enrollment is rejected as COURSE_FULL
        EnrollmentException ex = assertThrows(EnrollmentException.class,
                () -> service.enroll("S002", "CS101"));
        assertThat(ex.getReason()).isEqualTo(EnrollmentException.FailureReason.COURSE_FULL);
    }

    @Test
    void enroll_throwsAlreadyEnrolled_whenStudentEnrollsTwice() throws EnrollmentException {
        when(studentRepository.findById("S001"))
                .thenReturn(Optional.of(new StudentEntity("S001", "Alice", "alice@uni.edu", 3.9)));

        service.addCourse(new Course("CS201", "Data Structures", 4, 30));
        service.enroll("S001", "CS201");   // first enrollment succeeds

        EnrollmentException ex = assertThrows(EnrollmentException.class,
                () -> service.enroll("S001", "CS201"));   // second time, same student
        assertThat(ex.getReason()).isEqualTo(EnrollmentException.FailureReason.ALREADY_ENROLLED);
    }

    @Test
    void enroll_throwsNoSuchElement_whenCourseMissing() {
        when(studentRepository.findById("S001"))
                .thenReturn(Optional.of(new StudentEntity("S001", "Alice", "alice@uni.edu", 3.9)));

        assertThrows(NoSuchElementException.class,
                () -> service.enroll("S001", "NOPE"));   // course doesn't exist
    }

// ---------- Reporting logic ----------

    @Test
    void getHonorRoll_returnsQualifyingStudentsSortedByGpaDescending() {
        when(studentRepository.findAll()).thenReturn(List.of(
                new StudentEntity("S001", "Alice", "alice@uni.edu", 3.9),
                new StudentEntity("S002", "Bob",   "bob@uni.edu",   3.2),   // below threshold
                new StudentEntity("S003", "Carol", "carol@uni.edu", 3.7)));

        List<Student> honorRoll = service.getHonorRoll(3.5);

        assertThat(honorRoll).hasSize(2);                        // Bob (3.2) excluded
        assertThat(honorRoll.get(0).name()).isEqualTo("Alice");  // 3.9 first (descending)
        assertThat(honorRoll.get(1).name()).isEqualTo("Carol");  // 3.7 second
    }

    @Test
    void getAverageGpa_computesMeanAcrossAllStudents() {
        when(studentRepository.findAll()).thenReturn(List.of(
                new StudentEntity("S001", "Alice", "alice@uni.edu", 4.0),
                new StudentEntity("S002", "Bob",   "bob@uni.edu",   2.0)));

        assertThat(service.getAverageGpa()).isEqualTo(3.0);   // (4.0 + 2.0) / 2
    }

}
