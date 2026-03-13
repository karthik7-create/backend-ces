package com.example.backend_ces.service;

import com.example.backend_ces.entity.Course;
import com.example.backend_ces.entity.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    // NOTE: To enable real email sending, uncomment the JavaMailSender injection
    // and the sendEmail method below, and configure spring.mail.* properties.
    //
    // private final JavaMailSender mailSender;

    /**
     * Sends enrollment confirmation notification.
     * Currently simulated via console logging.
     * Replace with actual email sending in production.
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

        // Simulate email by logging
        log.info("📧 ════════════════════════════════════════════");
        log.info("📧 EMAIL NOTIFICATION");
        log.info("📧 To:      {}", student.getEmail());
        log.info("📧 Subject: {}", subject);
        log.info("📧 Body:\n{}", body);
        log.info("📧 ════════════════════════════════════════════");

        // Uncomment below to send actual email:
        // sendEmail(student.getEmail(), subject, body);
    }

    /*
    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("noreply@courseenrollment.com");
            mailSender.send(message);
            log.info("📧 Email sent to {}", to);
        } catch (Exception e) {
            log.error("❌ Failed to send email to {}: {}", to, e.getMessage());
        }
    }
    */
}
