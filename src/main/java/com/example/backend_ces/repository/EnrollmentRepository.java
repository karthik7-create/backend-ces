package com.example.backend_ces.repository;

import com.example.backend_ces.entity.Enrollment;
import com.example.backend_ces.entity.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    boolean existsByStudentIdAndCourseIdAndStatusNot(Long studentId, Long courseId, EnrollmentStatus status);

    long countByCourseIdAndStatus(Long courseId, EnrollmentStatus status);

    Optional<Enrollment> findByStudentIdAndCourseIdAndStatus(Long studentId, Long courseId, EnrollmentStatus status);

    @Query("SELECT e.course.id, COUNT(e) as cnt FROM Enrollment e " +
            "WHERE e.status = :status " +
            "GROUP BY e.course.id ORDER BY cnt DESC LIMIT 1")
    List<Object[]> findMostPopularCourse(@Param("status") EnrollmentStatus status);

    long countByStatus(EnrollmentStatus status);
}
