package com.example.coursesearch.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CourseController {

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
            "message", "Welcome to Course Search API",
            "status", "Application is running",
            "endpoints", "/api/courses"
        );
    }

    @GetMapping("/api/courses")
    public Map<String, String> courses() {
        return Map.of(
            "message", "Course API endpoint",
            "note", "Implementation coming soon",
            "health", "/actuator/health"
        );
    }
}
