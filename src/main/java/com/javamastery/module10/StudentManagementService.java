package com.javamastery.module10;

import com.javamastery.module10.persistence.StudentEntity;
import com.javamastery.module10.persistence.StudentJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class StudentManagementService {

    /** Logger for this class */
    private static final Logger logger = LoggerFactory.getLogger(StudentManagementService.class);

    /** Students are now persisted via the repository (was: in-memory Map) */
    private final StudentJpaRepository studentRepository;

    /** Course code → Course (still in-memory — deliberate scope boundary for Module 11) */
    private final Map<String, Course> courses = new HashMap<>();

    /** Course code → set of enrolled student IDs (still in-memory) */
    private final Map<String, Set<String>> enrollments = new HashMap<>();

    public StudentManagementService(StudentJpaRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // ── mapping between domain record and persistence entity ───────────────────

    private StudentEntity toEntity(Student student) {
        return new StudentEntity(student.id(), student.name(), student.email(), student.gpa());
    }

    private Student toRecord(StudentEntity entity) {
        return new Student(entity.getId(), entity.getName(), entity.getEmail(), entity.getGpa());
    }

    // ── Student management ─────────────────────────────────────────────────────

    public void addStudent(Student student) {
        if (studentRepository.existsById(student.id()))
            throw new IllegalArgumentException("Student already exists: " + student.id());
        studentRepository.save(toEntity(student));
        logger.info("Added student: {}", student.id());
    }

    public Student getStudent(String id) {
        return studentRepository.findById(id)
                .map(this::toRecord)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::toRecord)
                .toList();
    }

    // ── Course management (unchanged) ──────────────────────────────────────────

    public void addCourse(Course course) {
        if (courses.containsKey(course.code()))
            throw new IllegalArgumentException("Course with code '" + course.code() + "' already exists.");
        courses.put(course.code(), course);
        enrollments.put(course.code(), new HashSet<>());
    }

    public Course getCourse(String code) {
        if (!courses.containsKey(code))
            throw new NoSuchElementException("Course with code '" + code + "' does not exist.");
        return courses.get(code);
    }

    // ── Enrollment ─────────────────────────────────────────────────────────────

    public void enroll(String studentId, String courseCode) throws EnrollmentException {
        getStudent(studentId);                 // now validates via the repository
        Course course = getCourse(courseCode);
        Set<String> enrolledStudents = enrollments.get(courseCode);
        if (enrolledStudents.contains(studentId))
            throw new EnrollmentException(studentId, courseCode, EnrollmentException.FailureReason.ALREADY_ENROLLED);
        if (enrolledStudents.size() >= course.capacity())
            throw new EnrollmentException(studentId, courseCode, EnrollmentException.FailureReason.COURSE_FULL);
        enrolledStudents.add(studentId);
        logger.info("Student [{}] successfully enrolled in course [{}]", studentId, courseCode);
    }

    public List<Student> getEnrolledStudents(String courseCode) {
        return enrollments.getOrDefault(courseCode, Collections.emptySet())
                .stream()
                .map(this::getStudent)         // now fetches via the repository
                .toList();
    }

    public List<Course> getCoursesForStudent(String studentId) {
        return enrollments.entrySet().stream()
                .filter(entry -> entry.getValue().contains(studentId))
                .map(entry -> courses.get(entry.getKey()))
                .toList();
    }

    // ── Reporting ──────────────────────────────────────────────────────────────

    public double getAverageGpa() {
        return studentRepository.findAll().stream()
                .mapToDouble(StudentEntity::getGpa)
                .average()
                .orElse(0.0);
    }

    public List<Student> getHonorRoll(double threshold) {
        return studentRepository.findAll().stream()
                .map(this::toRecord)
                .filter(student -> student.gpa() >= threshold)
                .sorted(Comparator.comparingDouble(Student::gpa).reversed())
                .toList();
    }

    public Map<String, Integer> getEnrollmentCounts() {
        Map<String, Integer> enrollmentCounts = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : enrollments.entrySet()) {
            enrollmentCounts.put(entry.getKey(), entry.getValue().size());
        }
        return enrollmentCounts;
    }
}