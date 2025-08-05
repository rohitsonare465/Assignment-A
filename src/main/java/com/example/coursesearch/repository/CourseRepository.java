package com.example.coursesearch.repository;

import com.example.coursesearch.model.CourseDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends ElasticsearchRepository<CourseDocument, String> {
    
    Page<CourseDocument> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title, String description, Pageable pageable);
    
    Page<CourseDocument> findByCategory(String category, Pageable pageable);
    
    Page<CourseDocument> findByLevel(String level, Pageable pageable);
    
    Page<CourseDocument> findByInstructor(String instructor, Pageable pageable);
    
    Page<CourseDocument> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);
    
    Page<CourseDocument> findByRatingGreaterThanEqual(Float rating, Pageable pageable);
    
    Page<CourseDocument> findByTagsIn(List<String> tags, Pageable pageable);
    
    Page<CourseDocument> findByIsActiveTrue(Pageable pageable);
}
