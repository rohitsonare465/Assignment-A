package com.example.coursesearch.service;

import com.example.coursesearch.dto.CourseSearchRequest;
import com.example.coursesearch.dto.CourseSearchResponse;
import com.example.coursesearch.model.CourseDocument;
import com.example.coursesearch.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseSearchResponse searchCourses(CourseSearchRequest request) {
        log.info("Searching courses with request: {}", request);

        // Create sort
        Sort sort = Sort.unsorted();
        if (request.getSortBy() != null && !request.getSortBy().trim().isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(request.getSortDirection()) 
                ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, request.getSortBy());
        }

        // Create pageable
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CourseDocument> coursePage;

        // Simple implementation using repository methods
        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            coursePage = courseRepository.findByCategory(request.getCategory(), pageable);
        } else if (request.getQuery() != null && !request.getQuery().trim().isEmpty()) {
            // Use the existing search method in repository
            coursePage = courseRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                request.getQuery(), request.getQuery(), pageable);
        } else {
            // Get all courses if no specific search criteria
            coursePage = courseRepository.findAll(pageable);
        }

        // Filter results based on additional criteria (simple in-memory filtering for now)
        List<CourseDocument> filteredCourses = coursePage.getContent().stream()
            .filter(course -> {
                // Apply filters
                if (request.getType() != null && !request.getType().trim().isEmpty()) {
                    if (!request.getType().equalsIgnoreCase(course.getType())) {
                        return false;
                    }
                }
                if (request.getGradeRange() != null && !request.getGradeRange().trim().isEmpty()) {
                    if (!request.getGradeRange().equalsIgnoreCase(course.getGradeRange())) {
                        return false;
                    }
                }
                if (request.getMinAge() != null && course.getMinAge() != null) {
                    if (course.getMinAge() < request.getMinAge()) {
                        return false;
                    }
                }
                if (request.getMaxAge() != null && course.getMaxAge() != null) {
                    if (course.getMaxAge() > request.getMaxAge()) {
                        return false;
                    }
                }
                if (request.getMinPrice() != null && course.getPrice() != null) {
                    if (course.getPrice() < request.getMinPrice()) {
                        return false;
                    }
                }
                if (request.getMaxPrice() != null && course.getPrice() != null) {
                    if (course.getPrice() > request.getMaxPrice()) {
                        return false;
                    }
                }
                return true;
            })
            .collect(Collectors.toList());

        log.info("Found {} courses", filteredCourses.size());

        // Create response
        CourseSearchResponse response = new CourseSearchResponse();
        response.setCourses(filteredCourses);
        response.setTotalElements(coursePage.getTotalElements());
        response.setTotalPages(coursePage.getTotalPages());
        response.setCurrentPage(page);
        response.setPageSize(size);

        return response;
    }

    public CourseDocument saveCourse(CourseDocument course) {
        log.info("Saving course: {}", course.getTitle());
        return courseRepository.save(course);
    }

    public List<CourseDocument> saveAllCourses(List<CourseDocument> courses) {
        log.info("Saving {} courses", courses.size());
        return StreamSupport.stream(courseRepository.saveAll(courses).spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<CourseDocument> getAllCourses() {
        log.info("Getting all courses");
        return StreamSupport.stream(courseRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public void deleteAllCourses() {
        log.info("Deleting all courses");
        courseRepository.deleteAll();
    }

    public long countCourses() {
        return courseRepository.count();
    }

    public List<String> getAutocompleteSuggestions(String query) {
        log.info("Getting autocomplete suggestions for: {}", query);
        
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        
        // Get all courses and extract title suggestions
        List<CourseDocument> allCourses = getAllCourses();
        
        return allCourses.stream()
            .map(CourseDocument::getTitle)
            .filter(title -> title != null && title.toLowerCase().contains(query.toLowerCase()))
            .distinct()
            .limit(5) // Limit to 5 suggestions
            .collect(Collectors.toList());
    }
}
