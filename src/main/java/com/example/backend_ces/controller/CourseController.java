package com.example.backend_ces.controller;

import com.example.backend_ces.dto.ApiResponse;
import com.example.backend_ces.dto.CourseRequest;
import com.example.backend_ces.dto.CourseResponse;
import com.example.backend_ces.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course Management APIs")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new course", description = "Admin only: creates a new course",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CourseRequest request) {
        CourseResponse response = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Course created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all courses", description = "Returns a list of all available courses")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllCourses() {
        List<CourseResponse> courses = courseService.getAllCourses();
        return ResponseEntity.ok(ApiResponse.success("Courses retrieved successfully", courses));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID", description = "Returns details of a specific course")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable Long id) {
        CourseResponse response = courseService.getCourseById(id);
        return ResponseEntity.ok(ApiResponse.success("Course retrieved successfully", response));
    }

    @GetMapping("/search")
    @Operation(summary = "Search courses", description = "Search courses by keyword (name, instructor, or category)")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> searchCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {

        List<CourseResponse> courses;

        if (category != null && !category.isBlank()) {
            courses = courseService.getCoursesByCategory(category);
        } else if (keyword != null && !keyword.isBlank()) {
            courses = courseService.searchCourses(keyword);
        } else {
            courses = courseService.getAllCourses();
        }

        return ResponseEntity.ok(ApiResponse.success("Search results", courses));
    }
}
