package com.example.coursesearch.component;

import com.example.coursesearch.model.CourseDocument;
import com.example.coursesearch.service.CourseService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final CourseService courseService;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing sample course data...");
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            
            ClassPathResource resource = new ClassPathResource("sample-courses.json");
            InputStream inputStream = resource.getInputStream();
            
            List<CourseDocument> courses = objectMapper.readValue(
                    inputStream, 
                    new TypeReference<List<CourseDocument>>() {}
            );
            
            // Set the titleSuggest field for autocomplete
            courses.forEach(course -> {
                if (course.getTitle() != null) {
                    course.setTitleSuggest(course.getTitle());
                }
            });
            
            courseService.saveAllCourses(courses);
            log.info("Successfully loaded {} sample courses", courses.size());
            
        } catch (Exception e) {
            log.error("Failed to load sample course data", e);
        }
    }
}
