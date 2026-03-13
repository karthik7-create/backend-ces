package com.example.backend_ces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStats {

    private long totalStudents;
    private long totalCourses;
    private long totalEnrollments;
    private String mostPopularCourse;
    private long mostPopularCourseEnrollments;
}
