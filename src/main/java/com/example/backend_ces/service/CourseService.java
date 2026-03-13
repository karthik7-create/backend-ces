package com.example.backend_ces.service;

import com.example.backend_ces.dto.CourseRequest;
import com.example.backend_ces.dto.CourseResponse;
import com.example.backend_ces.entity.Course;
import com.example.backend_ces.entity.EnrollmentStatus;
import com.example.backend_ces.exception.CourseNotFoundException;
import com.example.backend_ces.repository.CourseRepository;
import com.example.backend_ces.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseResponse createCourse(CourseRequest request) {
        Course course = Course.builder()
                .courseName(request.getCourseName())
                .description(request.getDescription())
                .instructor(request.getInstructor())
                .capacity(request.getCapacity())
                .category(request.getCategory())
                .build();

        course = courseRepository.save(course);
        log.info("📚 Course created: {} (ID: {})", course.getCourseName(), course.getId());

        return mapToResponse(course);
    }

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + id));
        return mapToResponse(course);
    }

    public List<CourseResponse> searchCourses(String keyword) {
        return courseRepository.searchCourses(keyword)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CourseResponse> getCoursesByCategory(String category) {
        return courseRepository.findByCategory(category)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ─── Helper Methods ───

    private CourseResponse mapToResponse(Course course) {
        long enrolledCount = enrollmentRepository.countByCourseIdAndStatus(
                course.getId(), EnrollmentStatus.ENROLLED);

        return CourseResponse.builder()
                .id(course.getId())
                .courseName(course.getCourseName())
                .description(course.getDescription())
                .instructor(course.getInstructor())
                .capacity(course.getCapacity())
                .enrolledCount((int) enrolledCount)
                .availableSeats(course.getCapacity() - (int) enrolledCount)
                .category(course.getCategory())
                .createdAt(course.getCreatedAt())
                .build();
    }
}
