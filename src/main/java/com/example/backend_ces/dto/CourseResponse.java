package com.example.backend_ces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponse {

    private Long id;
    private String courseName;
    private String description;
    private String instructor;
    private Integer capacity;
    private Integer enrolledCount;
    private Integer availableSeats;
    private String category;
    private LocalDateTime createdAt;
}
