package com.example.backend_ces.service;

import com.example.backend_ces.entity.Course;
import com.example.backend_ces.entity.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;

    /**
     * Sends enrollment confirmation email to the student.
     */
    public void sendEnrollmentConfirmation(Student student, Course course) {
        String subject = "Enrollment Confirmation - " + course.getCourseName();
        String body = String.format(
                """
                Hi %s,
                
                You have been successfully enrolled in the course: %s
                Instructor: %s
                Category: %s
                
                Thank you for enrolling!
                
                — Course Enrollment System
                """,
                student.getName(),
                course.getCourseName(),
                course.getInstructor(),
                course.getCategory() != null ? course.getCategory() : "N/A"
        );

        sendEmail(student.getEmail(), subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            log.info("📧 Attempting to send email to {}...", to);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("karthikeyanr776@gmail.com");
            mailSender.send(message);
            log.info("📧 ✅ Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("❌ Failed to send email to {}: {}", to, e.getMessage(), e);
        }
    }
}
