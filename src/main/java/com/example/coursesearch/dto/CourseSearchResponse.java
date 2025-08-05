package com.example.coursesearch.dto;

import com.example.coursesearch.model.CourseDocument;
import lombok.Data;

import java.util.List;

@Data
public class CourseSearchResponse {
    private List<CourseDocument> courses;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
}
