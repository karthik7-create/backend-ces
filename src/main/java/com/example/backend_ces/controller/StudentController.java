package com.example.backend_ces.controller;

import com.example.backend_ces.dto.ApiResponse;
import com.example.backend_ces.entity.Student;
import com.example.backend_ces.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Student Profile APIs")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Returns profile of the authenticated user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProfile(
            @AuthenticationPrincipal Student student) {

        Map<String, Object> profile = Map.of(
                "id", student.getId(),
                "name", student.getName(),
                "email", student.getEmail(),
                "role", student.getRole().name(),
                "createdAt", student.getCreatedAt().toString()
        );

        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", profile));
    }
}
