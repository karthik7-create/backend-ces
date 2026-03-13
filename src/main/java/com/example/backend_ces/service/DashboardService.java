package com.example.backend_ces.service;

import com.example.backend_ces.dto.DashboardStats;
import com.example.backend_ces.entity.EnrollmentStatus;
import com.example.backend_ces.repository.CourseRepository;
import com.example.backend_ces.repository.EnrollmentRepository;
import com.example.backend_ces.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public DashboardStats getStats() {
        long totalStudents = studentRepository.count();
        long totalCourses = courseRepository.count();
        long totalEnrollments = enrollmentRepository.countByStatus(EnrollmentStatus.ENROLLED);

        // Find most popular course
        String mostPopularCourseName = "N/A";
        long mostPopularCount = 0;

        List<Object[]> result = enrollmentRepository.findMostPopularCourse(EnrollmentStatus.ENROLLED);
        if (!result.isEmpty()) {
            Object[] row = result.get(0);
            Long courseId = (Long) row[0];
            mostPopularCount = (Long) row[1];

            mostPopularCourseName = courseRepository.findById(courseId)
                    .map(course -> course.getCourseName())
                    .orElse("Unknown");
        }

        log.info("📊 Dashboard stats fetched — Students: {}, Courses: {}, Enrollments: {}",
                totalStudents, totalCourses, totalEnrollments);

        return DashboardStats.builder()
                .totalStudents(totalStudents)
                .totalCourses(totalCourses)
                .totalEnrollments(totalEnrollments)
                .mostPopularCourse(mostPopularCourseName)
                .mostPopularCourseEnrollments(mostPopularCount)
                .build();
    }
}
