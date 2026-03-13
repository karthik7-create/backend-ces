package com.example.backend_ces.controller;

import com.example.backend_ces.dto.ApiResponse;
import com.example.backend_ces.dto.EnrollmentRequest;
import com.example.backend_ces.dto.EnrollmentResponse;
import com.example.backend_ces.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Enrollment Management APIs")
@SecurityRequirement(name = "bearerAuth")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @Operation(summary = "Enroll in a course", description = "Enroll a student in a course")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enroll(@Valid @RequestBody EnrollmentRequest request) {
        EnrollmentResponse response = enrollmentService.enrollStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Enrollment successful", response));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get enrollments by student", description = "View all courses a student is enrolled in")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getByStudent(@PathVariable Long studentId) {
        List<EnrollmentResponse> enrollments = enrollmentService.getEnrollmentsByStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success("Enrollments retrieved", enrollments));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get enrollments by course", description = "View all students enrolled in a course")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getByCourse(@PathVariable Long courseId) {
        List<EnrollmentResponse> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success("Enrollments retrieved", enrollments));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Drop a course", description = "Drop/cancel an enrollment")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> dropEnrollment(@PathVariable Long id) {
        EnrollmentResponse response = enrollmentService.dropEnrollment(id);
        return ResponseEntity.ok(ApiResponse.success("Course dropped successfully", response));
    }
}
