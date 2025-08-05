package com.example.coursesearch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.CompletionField;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "courses")
public class CourseDocument {
    
    @Id
    private String id;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;
    
    /**
     * Completion field for Elasticsearch completion suggester.
     * Currently populated but not actively used - implementation uses substring matching instead.
     * This field is ready for future enhancement to use Elasticsearch's completion suggester API.
     */
    @CompletionField(maxInputLength = 100)
    private String titleSuggest;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;
    
    @Field(type = FieldType.Keyword)
    private String category;
    
    @Field(type = FieldType.Keyword)
    private String type; // ONE_TIME, COURSE, or CLUB
    
    @Field(type = FieldType.Keyword)
    private String gradeRange;
    
    @Field(type = FieldType.Integer)
    private Integer minAge;
    
    @Field(type = FieldType.Integer)
    private Integer maxAge;
    
    @Field(type = FieldType.Double)
    private Double price;
    
    @Field(type = FieldType.Keyword)
    private String nextSessionDate; // ISO-8601 format
    
    // Legacy fields for backward compatibility
    @Field(type = FieldType.Keyword)
    private String instructor;
    
    @Field(type = FieldType.Keyword)
    private String level;
    
    @Field(type = FieldType.Integer)
    private Integer duration;
    
    @Field(type = FieldType.Float)
    private Float rating;
    
    @Field(type = FieldType.Integer)
    private Integer enrollments;
    
    @Field(type = FieldType.Keyword)
    private List<String> tags;
    
    @Field(type = FieldType.Keyword)
    private String createdDate;
    
    @Field(type = FieldType.Keyword)
    private String updatedDate;
    
    @Field(type = FieldType.Boolean)
    private Boolean isActive;
}