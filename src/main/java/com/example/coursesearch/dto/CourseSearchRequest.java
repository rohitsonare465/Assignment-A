package com.example.coursesearch.dto;

import lombok.Data;

import java.util.List;

@Data
public class CourseSearchRequest {
    private String query;
    private String category;
    private String type; // ONE_TIME, COURSE, or CLUB
    private String gradeRange;
    private Integer minAge;
    private Integer maxAge;
    private Double minPrice;
    private Double maxPrice;
    private String nextSessionDate;
    
    // Legacy fields for backward compatibility
    private String level;
    private String instructor;
    private Float minRating;
    private List<String> tags;
    
    // Pagination and sorting
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "nextSessionDate";
    private String sortDirection = "asc";
}
