package com.example.backend_ces.service;

import com.example.backend_ces.dto.AuthResponse;
import com.example.backend_ces.dto.LoginRequest;
import com.example.backend_ces.dto.RegisterRequest;
import com.example.backend_ces.entity.Role;
import com.example.backend_ces.entity.Student;
import com.example.backend_ces.repository.StudentRepository;
import com.example.backend_ces.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        // Determine role
        Role role = Role.STUDENT;
        if (request.getRole() != null && request.getRole().equalsIgnoreCase("ADMIN")) {
            role = Role.ADMIN;
        }

        // Build student entity
        Student student = Student.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        studentRepository.save(student);
        log.info("✅ Student registered successfully: {}", student.getEmail());

        // Generate JWT token
        String token = jwtService.generateToken(student);

        return AuthResponse.builder()
                .token(token)
                .email(student.getEmail())
                .name(student.getName())
                .role(student.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Student student = studentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        String token = jwtService.generateToken(student);
        log.info("✅ Student logged in: {}", student.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(student.getEmail())
                .name(student.getName())
                .role(student.getRole().name())
                .build();
    }
}
