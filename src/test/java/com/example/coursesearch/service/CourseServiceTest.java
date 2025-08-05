package com.example.coursesearch.service;

import com.example.coursesearch.dto.CourseSearchRequest;
import com.example.coursesearch.dto.CourseSearchResponse;
import com.example.coursesearch.model.CourseDocument;
import com.example.coursesearch.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @Mock
    private SearchHits<CourseDocument> searchHits;

    private CourseService courseService;

    @BeforeEach
    void setUp() {
        courseService = new CourseService(courseRepository, elasticsearchOperations);
    }

    @Test
    void testSearchCourses_WithBasicQuery_ShouldReturnResults() {
        // Given
        CourseSearchRequest request = new CourseSearchRequest();
        request.setQuery("math");
        request.setPage(0);
        request.setSize(10);

        CourseDocument course1 = createTestCourse("1", "Math Adventures", "Math", 85.0);
        CourseDocument course2 = createTestCourse("2", "Advanced Mathematics", "Math", 135.0);
        
        @SuppressWarnings("unchecked")
        SearchHit<CourseDocument> hit1 = mock(SearchHit.class);
        @SuppressWarnings("unchecked")
        SearchHit<CourseDocument> hit2 = mock(SearchHit.class);
        
        when(hit1.getContent()).thenReturn(course1);
        when(hit2.getContent()).thenReturn(course2);

        when(searchHits.getTotalHits()).thenReturn(2L);
        when(searchHits.getSearchHits()).thenReturn(Arrays.asList(hit1, hit2));
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHits);

        // When
        CourseSearchResponse response = courseService.searchCourses(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(2L);
        assertThat(response.getCourses()).hasSize(2);
        assertThat(response.getCurrentPage()).isEqualTo(0);
        assertThat(response.getPageSize()).isEqualTo(10);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.isHasNext()).isFalse();
        assertThat(response.isHasPrevious()).isFalse();
    }

    @Test
    void testSearchCourses_WithPagination_ShouldCalculateCorrectMetadata() {
        // Given
        CourseSearchRequest request = new CourseSearchRequest();
        request.setPage(1); // Second page
        request.setSize(5);

        CourseDocument course = createTestCourse("6", "Course 6", "Math", 100.0);
        
        @SuppressWarnings("unchecked")
        SearchHit<CourseDocument> hit = mock(SearchHit.class);
        when(hit.getContent()).thenReturn(course);

        when(searchHits.getTotalHits()).thenReturn(12L); // 12 total courses
        when(searchHits.getSearchHits()).thenReturn(Arrays.asList(hit));
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHits);

        // When
        CourseSearchResponse response = courseService.searchCourses(request);

        // Then
        assertThat(response.getTotalElements()).isEqualTo(12L);
        assertThat(response.getCurrentPage()).isEqualTo(1);
        assertThat(response.getPageSize()).isEqualTo(5);
        assertThat(response.getTotalPages()).isEqualTo(3); // 12 courses / 5 per page = 3 pages
        assertThat(response.isHasNext()).isTrue();  // Not on last page (page 1 of 3)
        assertThat(response.isHasPrevious()).isTrue(); // Not on first page
    }

    @Test
    void testAutocompleteSuggestions_WithValidQuery_ShouldReturnMatchingTitles() {
        // Given
        String query = "art";
        List<CourseDocument> allCourses = Arrays.asList(
            createTestCourse("1", "Young Artists Club", "Art", 85.0),
            createTestCourse("2", "Digital Art Workshop", "Art", 95.0),
            createTestCourse("3", "Mathematics Fun", "Math", 75.0),
            createTestCourse("4", "Martial Arts Training", "Sports", 120.0)
        );

        when(courseRepository.findAll()).thenReturn(allCourses);

        // When
        List<String> suggestions = courseService.getAutocompleteSuggestions(query);

        // Then
        assertThat(suggestions).hasSize(3);
        assertThat(suggestions).contains(
            "Young Artists Club",
            "Digital Art Workshop", 
            "Martial Arts Training"
        );
        assertThat(suggestions).doesNotContain("Mathematics Fun");
    }

    @Test
    void testAutocompleteSuggestions_WithEmptyQuery_ShouldReturnEmptyList() {
        // When
        List<String> suggestions = courseService.getAutocompleteSuggestions("");

        // Then
        assertThat(suggestions).isEmpty();
    }

    @Test
    void testAutocompleteSuggestions_WithNullQuery_ShouldReturnEmptyList() {
        // When
        List<String> suggestions = courseService.getAutocompleteSuggestions(null);

        // Then
        assertThat(suggestions).isEmpty();
    }

    @Test
    void testSearchCourses_WithEmptyResult_ShouldReturnZeroElements() {
        // Given
        CourseSearchRequest request = new CourseSearchRequest();
        request.setCategory("NonExistent");
        request.setPage(0);
        request.setSize(10);

        when(searchHits.getTotalHits()).thenReturn(0L);
        when(searchHits.getSearchHits()).thenReturn(Arrays.asList());
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHits);

        // When
        CourseSearchResponse response = courseService.searchCourses(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(0L);
        assertThat(response.getCourses()).isEmpty();
        assertThat(response.getCurrentPage()).isEqualTo(0);
        assertThat(response.getTotalPages()).isEqualTo(0);
        assertThat(response.isHasNext()).isFalse();
        assertThat(response.isHasPrevious()).isFalse();
    }

    private CourseDocument createTestCourse(String id, String title, String category, double price) {
        CourseDocument course = new CourseDocument();
        course.setId(id);
        course.setTitle(title);
        course.setCategory(category);
        course.setPrice(price);
        course.setNextSessionDate("2025-06-15T10:00:00Z");
        course.setDescription("Test description for " + title);
        course.setType("COURSE");
        course.setGradeRange("K-12");
        course.setMinAge(5);
        course.setMaxAge(17);
        return course;
    }
}
