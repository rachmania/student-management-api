package com.javamastery.module10;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * The core service of the Student Management System.
 * Manages students, courses, and enrollments — combining collections,
 * streams, Optional, and custom exceptions into a cohesive application.
 *
 * Data model:
 * <ul>
 *   <li>{@code students}    — maps student ID to Student</li>
 *   <li>{@code courses}     — maps course code to Course</li>
 *   <li>{@code enrollments} — maps course code to the set of enrolled student IDs</li>
 * </ul>
 *
 * @author Rachman Walker
 * @version 1.0
 * @since Module 10
 */
public class StudentManagementService {

    /** Logger for this class */
    private static final Logger logger = LoggerFactory.getLogger(StudentManagementService.class);

    /** Student ID → Student */
    private final Map<String, Student> students = new HashMap<>();

    /** Course code → Course */
    private final Map<String, Course> courses = new HashMap<>();

    /** Course code → set of enrolled student IDs */
    private final Map<String, Set<String>> enrollments = new HashMap<>();

    // ── Student management ────────────────────────────────────────────────────

    /**
     * Adds a student to the system.
     *
     * @param student the student to add
     * @throws IllegalArgumentException if a student with the same ID already exists
     */
    public void addStudent(Student student) {
        if (students.containsKey(student.id()))
            throw new IllegalArgumentException("Student already exists: " + student.id());
        students.put(student.id(), student);
        logger.info("Added student: {}", student.id());
    }


    /**
     * Retrieves a student by ID.
     *
     * @param id the student ID
     * @return the matching student
     * @throws StudentNotFoundException if no student has that ID
     */
    public Student getStudent(String id) {
        // if the map doesn't contain id, throw StudentNotFoundException
        if (!students.containsKey(id))
            throw new StudentNotFoundException(id);
        // otherwise return the student
        return students.get(id);
    }

    /**
     * Returns all students as an unmodifiable list.
     *
     * @return a list of all students
     */
    public List<Student> getAllStudents() {
        // return an unmodifiable list built from students.values()
        return List.copyOf(students.values());
    }

    // ── Course management ─────────────────────────────────────────────────────

    /**
     * Adds a course to the system and initialises its (empty) enrollment set.
     *
     * @param course the course to add
     * @throws IllegalArgumentException if a course with the same code already exists
     */
    public void addCourse(Course course) {
        // if courses already contains course.code(), throw IllegalArgumentException
        if(courses.containsKey(course.code()))
            throw new IllegalArgumentException("Course with code '" + course.code() + "' already exists.");
        // otherwise put the course in 'courses' AND initialise an empty set in 'enrollments'
        courses.put(course.code(), course);
        enrollments.put(course.code(), new HashSet<>());
    }

    /**
     * Retrieves a course by code.
     *
     * @param code the course code
     * @return the matching course
     * @throws NoSuchElementException if no course has that code
     */
    public Course getCourse(String code) {
        if (!courses.containsKey(code))
            throw new NoSuchElementException("Course with code '" + code + "' does not exist.");
        return courses.get(code);
    }

    // ── Enrollment ────────────────────────────────────────────────────────────

    /**
     * Enrolls a student in a course.
     * Validates the student exists, the course exists, the student isn't
     * already enrolled, and the course isn't full.
     *
     * @param studentId  the student's ID
     * @param courseCode the course code
     * @throws StudentNotFoundException if the student does not exist
     * @throws EnrollmentException      if already enrolled or the course is full
     */
    public void enroll(String studentId, String courseCode) throws EnrollmentException {
        // 1. Validate the student exists — throws StudentNotFoundException if missing
        getStudent(studentId);
        // 2. Fetch the course (needed for the capacity check)
        Course course = getCourse(courseCode);
        // 3. Get the enrolled set for this course
        Set<String> enrolledStudents = enrollments.get(courseCode);
        // 4. if the set already contains studentId → throw EnrollmentException(ALREADY_ENROLLED)
        if(enrolledStudents.contains(studentId))
            throw new EnrollmentException(studentId, courseCode, EnrollmentException.FailureReason.ALREADY_ENROLLED);
        // 5. if the set size >= course.capacity() → throw EnrollmentException(COURSE_FULL)
        if(enrolledStudents.size() >= course.capacity())
            throw new EnrollmentException(studentId, courseCode, EnrollmentException.FailureReason.COURSE_FULL);
        // 6. otherwise add studentId to the set and log success
        enrolledStudents.add(studentId);
        logger.info("Student [{}] successfully enrolled in course [{}]", studentId, courseCode);
    }

    /**
     * Returns the students enrolled in a given course.
     *
     * @param courseCode the course code
     * @return a list of enrolled students (empty if none)
     */
    public List<Student> getEnrolledStudents(String courseCode) {
        // get the set of student IDs for this course (empty set if absent)
        // stream the IDs, map each to its Student via students.get(), collect to a list
        return enrollments.getOrDefault(courseCode, Collections.emptySet())
                .stream()
                .map(students::get)
                .toList();
    }

    /**
     * Returns the courses a given student is enrolled in.
     *
     * @param studentId the student ID
     * @return a list of courses the student is enrolled in
     */
    public List<Course> getCoursesForStudent(String studentId) {
        // stream enrollments.entrySet()
        // keep entries whose value (the set) contains studentId
        // map each entry's key (course code) to its Course
        // collect to a list
        return enrollments.entrySet().stream()
                .filter(entry -> entry.getValue().contains(studentId))
                .map(entry -> courses.get(entry.getKey()))
                .toList();
    }

    // ── Reporting (streams) ───────────────────────────────────────────────────

    /**
     * Calculates the average GPA across all students.
     *
     * @return the average GPA, or 0.0 if there are no students
     */
    public double getAverageGpa() {
        // stream students.values(), mapToDouble(Student::gpa), average().orElse(0.0)
        return students.values().stream()
                .mapToDouble(Student::gpa)
                .average()
                .orElse(0.0);
    }

    /**
     * Returns students on the honor roll — those with a GPA at or above the threshold.
     *
     * @param threshold the minimum GPA to qualify
     * @return a list of qualifying students, sorted by GPA descending
     */
    public List<Student> getHonorRoll(double threshold) {
        // stream students.values()
        // filter gpa >= threshold
        // sorted by gpa descending (Comparator.comparingDouble(Student::gpa).reversed())
        // collect to a list
        return students.values().stream()
                .filter(student -> student.gpa() >= threshold)
                .sorted(Comparator.comparingDouble(Student::gpa).reversed())
                .toList();
    }

    /**
     * Returns the number of students enrolled in each course.
     *
     * @return a map of course code to enrollment count
     */
    public Map<String, Integer> getEnrollmentCounts() {
        // build a map of courseCode → size of its enrolled set
        // (stream enrollments.entrySet() and collect, or loop and put)
        Map<String, Integer> enrollmentCounts = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : enrollments.entrySet()) {
            enrollmentCounts.put(entry.getKey(), entry.getValue().size());
        }
        return enrollmentCounts;
    }
}
