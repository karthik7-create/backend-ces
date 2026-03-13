package com.example.backend_ces.service;

import com.example.backend_ces.dto.EnrollmentRequest;
import com.example.backend_ces.dto.EnrollmentResponse;
import com.example.backend_ces.entity.Course;
import com.example.backend_ces.entity.Enrollment;
import com.example.backend_ces.entity.EnrollmentStatus;
import com.example.backend_ces.entity.Student;
import com.example.backend_ces.exception.*;
import com.example.backend_ces.repository.CourseRepository;
import com.example.backend_ces.repository.EnrollmentRepository;
import com.example.backend_ces.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final NotificationService notificationService;

    @Transactional
    public EnrollmentResponse enrollStudent(EnrollmentRequest request) {
        // 1. Validate student exists
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new StudentNotFoundException(
                        "Student not found with ID: " + request.getStudentId()));

        // 2. Validate course exists
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException(
                        "Course not found with ID: " + request.getCourseId()));

        // 3. Check for duplicate enrollment (not already ENROLLED)
        boolean alreadyEnrolled = enrollmentRepository
                .existsByStudentIdAndCourseIdAndStatusNot(
                        request.getStudentId(),
                        request.getCourseId(),
                        EnrollmentStatus.DROPPED);

        if (alreadyEnrolled) {
            throw new AlreadyEnrolledException(
                    "Student is already enrolled in course: " + course.getCourseName());
        }

        // 4. Check course capacity
        long currentEnrollments = enrollmentRepository.countByCourseIdAndStatus(
                request.getCourseId(), EnrollmentStatus.ENROLLED);

        if (currentEnrollments >= course.getCapacity()) {
            throw new CourseFullException(
                    "Course '" + course.getCourseName() + "' is full. Capacity: " + course.getCapacity());
        }

        // 5. Create enrollment
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .status(EnrollmentStatus.ENROLLED)
                .build();

        enrollment = enrollmentRepository.save(enrollment);
        log.info("🎓 Student '{}' enrolled in course '{}'", student.getName(), course.getCourseName());

        // 6. Send notification
        notificationService.sendEnrollmentConfirmation(student, course);

        return mapToResponse(enrollment);
    }

    public List<EnrollmentResponse> getEnrollmentsByStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException("Student not found with ID: " + studentId);
        }

        return enrollmentRepository.findByStudentId(studentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new CourseNotFoundException("Course not found with ID: " + courseId);
        }

        return enrollmentRepository.findByCourseId(courseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public EnrollmentResponse dropEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException(
                        "Enrollment not found with ID: " + enrollmentId));

        if (enrollment.getStatus() == EnrollmentStatus.DROPPED) {
            throw new IllegalArgumentException("Enrollment is already dropped");
        }

        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollment = enrollmentRepository.save(enrollment);

        log.info("❌ Student '{}' dropped course '{}'",
                enrollment.getStudent().getName(),
                enrollment.getCourse().getCourseName());

        return mapToResponse(enrollment);
    }

    // ─── Helper Methods ───

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudent().getId())
                .studentName(enrollment.getStudent().getName())
                .studentEmail(enrollment.getStudent().getEmail())
                .courseId(enrollment.getCourse().getId())
                .courseName(enrollment.getCourse().getCourseName())
                .instructor(enrollment.getCourse().getInstructor())
                .status(enrollment.getStatus().name())
                .enrolledAt(enrollment.getEnrolledAt())
                .build();
    }
}
