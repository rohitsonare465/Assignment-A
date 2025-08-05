package com.example.coursesearch.controller;

import com.example.coursesearch.dto.CourseSearchRequest;
import com.example.coursesearch.dto.CourseSearchResponse;
import com.example.coursesearch.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SearchController {
    
    private final CourseService courseService;
    
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCourses(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String startDate,
            @RequestParam(defaultValue = "upcoming") String sort,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("Received search request - q: {}, category: {}, sort: {}, page: {}, size: {}", 
                query, category, sort, page, size);
        
        // Map the request parameters to our internal CourseSearchRequest format
        CourseSearchRequest request = new CourseSearchRequest();
        request.setQuery(query);
        request.setMinAge(minAge);
        request.setMaxAge(maxAge);
        request.setCategory(category);
        request.setType(type);
        request.setMinPrice(minPrice);
        request.setMaxPrice(maxPrice);
        request.setNextSessionDate(startDate);
        request.setPage(page);
        request.setSize(size);
        
        // Map sort parameter to our internal format
        String sortBy = mapSortParameter(sort);
        String sortDirection = getSortDirection(sort);
        request.setSortBy(sortBy);
        request.setSortDirection(sortDirection);
        
        // Execute search using our service
        CourseSearchResponse response = courseService.searchCourses(request);
        
        // Transform response to match the requested format
        Map<String, Object> result = new HashMap<>();
        result.put("total", response.getTotalElements());
        
        // Extract only the required fields from courses
        List<Map<String, Object>> courses = response.getCourses().stream()
                .map(course -> {
                    Map<String, Object> courseMap = new HashMap<>();
                    courseMap.put("id", course.getId());
                    courseMap.put("title", course.getTitle());
                    courseMap.put("category", course.getCategory());
                    courseMap.put("price", course.getPrice());
                    courseMap.put("nextSessionDate", course.getNextSessionDate());
                    
                    // Include additional useful fields
                    courseMap.put("description", course.getDescription());
                    courseMap.put("type", course.getType());
                    courseMap.put("gradeRange", course.getGradeRange());
                    courseMap.put("minAge", course.getMinAge());
                    courseMap.put("maxAge", course.getMaxAge());
                    
                    return courseMap;
                })
                .collect(Collectors.toList());
        
        result.put("courses", courses);
        
        // Include pagination metadata for convenience
        result.put("pagination", Map.of(
            "currentPage", response.getCurrentPage(),
            "pageSize", response.getPageSize(),
            "totalPages", response.getTotalPages(),
            "hasNext", response.isHasNext(),
            "hasPrevious", response.isHasPrevious()
        ));
        
        log.info("Returning {} courses out of {} total", courses.size(), response.getTotalElements());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Maps the sort parameter to our internal sortBy field
     */
    private String mapSortParameter(String sort) {
        if (sort == null) {
            return "nextSessionDate";
        }
        
        switch (sort.toLowerCase()) {
            case "upcoming":
                return "nextSessionDate";
            case "priceasc":
                return "priceAsc";
            case "pricedesc":
                return "priceDesc";
            default:
                return "nextSessionDate"; // Default to upcoming
        }
    }
    
    /**
     * Gets the sort direction for the given sort parameter
     */
    private String getSortDirection(String sort) {
        if (sort == null) {
            return "asc";
        }
        
        switch (sort.toLowerCase()) {
            case "upcoming":
                return "asc"; // Earliest dates first
            case "priceasc":
                return "asc"; // Will be handled by special case in service
            case "pricedesc":
                return "desc"; // Will be handled by special case in service
            default:
                return "asc";
        }
    }
    
    @GetMapping("/search/help")
    public ResponseEntity<Map<String, Object>> getSearchHelp() {
        Map<String, Object> help = new HashMap<>();
        
        help.put("endpoint", "GET /api/search");
        help.put("description", "Search for courses with various filters and sorting options");
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", "Search keyword (searches title and description)");
        parameters.put("minAge", "Minimum age filter (integer)");
        parameters.put("maxAge", "Maximum age filter (integer)");
        parameters.put("category", "Course category filter (exact match)");
        parameters.put("type", "Course type filter: ONE_TIME, COURSE, or CLUB");
        parameters.put("minPrice", "Minimum price filter (decimal)");
        parameters.put("maxPrice", "Maximum price filter (decimal)");
        parameters.put("startDate", "Filter courses on or after this date (ISO-8601 format)");
        parameters.put("sort", "Sort order: upcoming (default), priceAsc, priceDesc");
        parameters.put("page", "Page number (default: 0)");
        parameters.put("size", "Page size (default: 10)");
        
        help.put("parameters", parameters);
        
        Map<String, String> response = new HashMap<>();
        response.put("total", "Total number of matching courses");
        response.put("courses", "Array of course objects with id, title, category, price, nextSessionDate, and more");
        response.put("pagination", "Pagination metadata including currentPage, pageSize, totalPages, hasNext, hasPrevious");
        
        help.put("response", response);
        
        Map<String, String> examples = new HashMap<>();
        examples.put("Basic search", "/api/search?q=math");
        examples.put("Filter by category", "/api/search?category=Science&sort=priceAsc");
        examples.put("Age and price range", "/api/search?minAge=10&maxAge=15&minPrice=50&maxPrice=100");
        examples.put("Pagination", "/api/search?page=1&size=5");
        examples.put("Combined filters", "/api/search?q=art&type=CLUB&sort=upcoming&page=0&size=10");
        
        help.put("examples", examples);
        
        return ResponseEntity.ok(help);
    }
}
