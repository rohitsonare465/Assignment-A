package com.example.coursesearch.controller;

import com.example.coursesearch.component.DataInitializer;
import com.example.coursesearch.dto.CourseSearchRequest;
import com.example.coursesearch.dto.CourseSearchResponse;
import com.example.coursesearch.model.CourseDocument;
import com.example.coursesearch.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CourseController {
    
    private final CourseService courseService;
    private final DataInitializer dataInitializer;
    
    @PostMapping("/load-sample-data")
    public ResponseEntity<Map<String, Object>> loadSampleData() {
        try {
            log.info("Manually triggering sample data load...");
            dataInitializer.run();
            return ResponseEntity.ok(Map.of(
                "message", "Sample data loaded successfully",
                "status", "success"
            ));
        } catch (Exception e) {
            log.error("Failed to load sample data", e);
            return ResponseEntity.status(500).body(Map.of(
                "message", "Failed to load sample data: " + e.getMessage(),
                "status", "error"
            ));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<CourseSearchResponse> searchCourses(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String gradeRange,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String nextSessionDate,
            @RequestParam(required = false) String level, // legacy
            @RequestParam(required = false) String instructor, // legacy
            @RequestParam(required = false) Float minRating, // legacy
            @RequestParam(required = false) List<String> tags, // legacy
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "nextSessionDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        log.info("Received search request for query: {}", query);
        
        CourseSearchRequest request = new CourseSearchRequest();
        request.setQuery(query);
        request.setCategory(category);
        request.setType(type);
        request.setGradeRange(gradeRange);
        request.setMinAge(minAge);
        request.setMaxAge(maxAge);
        request.setMinPrice(minPrice);
        request.setMaxPrice(maxPrice);
        request.setNextSessionDate(nextSessionDate);
        request.setLevel(level);
        request.setInstructor(instructor);
        request.setMinRating(minRating);
        request.setTags(tags);
        request.setPage(page);
        request.setSize(size);
        request.setSortBy(sortBy);
        request.setSortDirection(sortDirection);
        
        CourseSearchResponse response = courseService.searchCourses(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/search")
    public ResponseEntity<CourseSearchResponse> searchCoursesPost(@RequestBody CourseSearchRequest request) {
        log.info("Received POST search request: {}", request);
        CourseSearchResponse response = courseService.searchCourses(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> getAutocompleteSuggestions(@RequestParam String query) {
        log.info("Received autocomplete request for: {}", query);
        List<String> suggestions = courseService.getAutocompleteSuggestions(query);
        return ResponseEntity.ok(suggestions);
    }
    
    @PostMapping("/bulk")
    public ResponseEntity<List<CourseDocument>> createCourses(@RequestBody List<CourseDocument> courses) {
        log.info("Creating {} courses", courses.size());
        List<CourseDocument> savedCourses = courseService.saveAllCourses(courses);
        return ResponseEntity.ok(savedCourses);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Course Search API is running!");
    }
}