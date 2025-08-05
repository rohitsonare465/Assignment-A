package com.example.coursesearch.service;

import com.example.coursesearch.dto.CourseSearchRequest;
import com.example.coursesearch.dto.CourseSearchResponse;
import com.example.coursesearch.model.CourseDocument;
import com.example.coursesearch.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public CourseSearchResponse searchCourses(CourseSearchRequest request) {
        log.info("Searching courses with request: {}", request);

        // Create sort based on requirements
        Sort sort = createSort(request);

        // Create pageable
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size, sort);

        // Build Elasticsearch query using Criteria API
        CriteriaQuery query = buildSearchQuery(request);
        query.setPageable(pageable);

        // Execute search
        SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(query, CourseDocument.class);
        
        // Convert search hits to list
        List<CourseDocument> courses = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        log.info("Found {} courses out of {} total", courses.size(), searchHits.getTotalHits());

        // Create response
        CourseSearchResponse response = new CourseSearchResponse();
        response.setCourses(courses);
        response.setTotalElements(searchHits.getTotalHits());
        response.setTotalPages((int) Math.ceil((double) searchHits.getTotalHits() / size));
        response.setCurrentPage(page);
        response.setPageSize(size);
        response.setHasNext(page < response.getTotalPages() - 1);
        response.setHasPrevious(page > 0);

        return response;
    }

    private Sort createSort(CourseSearchRequest request) {
        // Handle special sort parameters
        if ("priceAsc".equals(request.getSortBy())) {
            return Sort.by(Sort.Direction.ASC, "price");
        } else if ("priceDesc".equals(request.getSortBy())) {
            return Sort.by(Sort.Direction.DESC, "price");
        }
        
        // Default sort: ascending by nextSessionDate (soonest upcoming first)
        if (request.getSortBy() == null || request.getSortBy().trim().isEmpty()) {
            return Sort.by(Sort.Direction.ASC, "nextSessionDate");
        }
        
        // Custom sort by provided field and direction
        Sort.Direction direction = "desc".equalsIgnoreCase(request.getSortDirection()) 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, request.getSortBy());
    }

    private CriteriaQuery buildSearchQuery(CourseSearchRequest request) {
        List<Criteria> criteriaList = new ArrayList<>();

        // Full-text search on title and description (multi-match equivalent)
        if (request.getQuery() != null && !request.getQuery().trim().isEmpty()) {
            Criteria textSearch = new Criteria("title").contains(request.getQuery())
                    .or(new Criteria("description").contains(request.getQuery()));
            criteriaList.add(textSearch);
        }

        // Exact filters for category
        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            criteriaList.add(new Criteria("category").is(request.getCategory()));
        }

        // Exact filters for type
        if (request.getType() != null && !request.getType().trim().isEmpty()) {
            criteriaList.add(new Criteria("type").is(request.getType()));
        }

        // Exact filters for gradeRange
        if (request.getGradeRange() != null && !request.getGradeRange().trim().isEmpty()) {
            criteriaList.add(new Criteria("gradeRange").is(request.getGradeRange()));
        }

        // Range filters for age - check if course age range overlaps with requested age range
        if (request.getMinAge() != null) {
            // Course max age should be >= requested min age (course accepts students at least as old as requested)
            criteriaList.add(new Criteria("maxAge").greaterThanEqual(request.getMinAge()));
        }
        if (request.getMaxAge() != null) {
            // Course min age should be <= requested max age (course accepts students no older than requested)
            criteriaList.add(new Criteria("minAge").lessThanEqual(request.getMaxAge()));
        }

        // Range filters for price
        if (request.getMinPrice() != null) {
            criteriaList.add(new Criteria("price").greaterThanEqual(request.getMinPrice()));
        }
        if (request.getMaxPrice() != null) {
            criteriaList.add(new Criteria("price").lessThanEqual(request.getMaxPrice()));
        }

        // Date filter for nextSessionDate (courses on or after given date)
        if (request.getNextSessionDate() != null && !request.getNextSessionDate().trim().isEmpty()) {
            try {
                // Parse the date string and create criteria
                criteriaList.add(new Criteria("nextSessionDate").greaterThanEqual(request.getNextSessionDate()));
            } catch (Exception e) {
                log.warn("Invalid date format for nextSessionDate: {}", request.getNextSessionDate());
            }
        }

        // Combine all criteria with AND logic
        if (criteriaList.isEmpty()) {
            return new CriteriaQuery(new Criteria());
        }

        Criteria combinedCriteria = criteriaList.get(0);
        for (int i = 1; i < criteriaList.size(); i++) {
            combinedCriteria = combinedCriteria.and(criteriaList.get(i));
        }

        return new CriteriaQuery(combinedCriteria);
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
        
        // Use a simple prefix search on the title field for autocomplete
        // In a real production system, you could implement Elasticsearch completion suggester
        try {
            Criteria titleCriteria = new Criteria("title").contains(query);
            CriteriaQuery searchQuery = new CriteriaQuery(titleCriteria);
            searchQuery.setPageable(PageRequest.of(0, 10));
            
            SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(searchQuery, CourseDocument.class);
            
            return searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent().getTitle())
                .filter(title -> title != null)
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Elasticsearch search failed, using fallback: {}", e.getMessage());
            
            // Fallback to simple in-memory filtering
            List<CourseDocument> allCourses = getAllCourses();
            
            return allCourses.stream()
                .map(CourseDocument::getTitle)
                .filter(title -> title != null && title.toLowerCase().contains(query.toLowerCase()))
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
        }
    }
}
