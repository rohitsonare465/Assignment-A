package com.example.coursesearch.integration;

import com.example.coursesearch.model.CourseDocument;
import com.example.coursesearch.service.CourseService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWebMvc
public class CourseSearchIntegrationTest {

    @Container
    static final ElasticsearchContainer elasticsearch = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.11.0")
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("xpack.security.enrollment.enabled", "false");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", elasticsearch::getHttpHostAddress);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @BeforeEach
    void setUp() throws Exception {
        baseUrl = "http://localhost:" + port;
        
        // Clear existing data
        courseService.deleteAllCourses();
        
        // Wait for Elasticsearch to be ready
        Thread.sleep(1000);
        
        // Index test data
        setupTestData();
        
        // Wait for indexing to complete
        Thread.sleep(2000);
    }

    private void setupTestData() {
        List<CourseDocument> testCourses = Arrays.asList(
            createCourse("1", "Beginner Math Adventures", "Fun math for young learners with counting and shapes", 
                        "Math", "COURSE", "K-2nd", 5, 8, 75.0, "2025-06-15T10:00:00Z"),
            
            createCourse("2", "Advanced Mathematics Challenge", "Challenging math for gifted students covering algebra and geometry", 
                        "Math", "COURSE", "7th-12th", 12, 17, 135.0, "2025-07-01T15:00:00Z"),
            
            createCourse("3", "Science Spectacular Workshop", "Exciting science experiments and demonstrations", 
                        "Science", "ONE_TIME", "3rd-8th", 8, 13, 25.0, "2025-06-20T14:00:00Z"),
            
            createCourse("4", "Robotics Engineering Club", "Build and program robots in this hands-on club", 
                        "Technology", "CLUB", "6th-12th", 11, 17, 180.0, "2025-06-25T16:00:00Z"),
            
            createCourse("5", "Young Artists Painting", "Creative painting workshop for young artists", 
                        "Art", "COURSE", "1st-5th", 6, 10, 85.0, "2025-06-18T16:30:00Z"),
            
            createCourse("6", "Budget-Friendly Craft Time", "Low-cost crafting activities for all ages", 
                        "Art", "ONE_TIME", "All Ages", 3, 15, 15.0, "2025-06-10T13:00:00Z"),
            
            createCourse("7", "Premium Photography Course", "Professional photography techniques and equipment", 
                        "Technology", "COURSE", "9th-12th", 14, 18, 250.0, "2025-07-15T18:00:00Z"),
            
            createCourse("8", "Soccer Skills Development", "Soccer training for young athletes", 
                        "Sports", "CLUB", "K-4th", 5, 9, 120.0, "2025-06-12T10:00:00Z")
        );
        
        courseService.saveAllCourses(testCourses);
    }

    private CourseDocument createCourse(String id, String title, String description, String category, 
                                      String type, String gradeRange, int minAge, int maxAge, 
                                      double price, String nextSessionDate) {
        CourseDocument course = new CourseDocument();
        course.setId(id);
        course.setTitle(title);
        course.setDescription(description);
        course.setCategory(category);
        course.setType(type);
        course.setGradeRange(gradeRange);
        course.setMinAge(minAge);
        course.setMaxAge(maxAge);
        course.setPrice(price);
        course.setNextSessionDate(nextSessionDate);
        return course;
    }

    @Test
    void testBasicSearch_ShouldReturnAllCourses() throws Exception {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/search", String.class);
        
        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        assertThat(jsonResponse.get("total").asInt()).isEqualTo(8);
        assertThat(jsonResponse.get("courses").size()).isEqualTo(8);
        
        // Verify response structure
        JsonNode firstCourse = jsonResponse.get("courses").get(0);
        assertThat(firstCourse.has("id")).isTrue();
        assertThat(firstCourse.has("title")).isTrue();
        assertThat(firstCourse.has("category")).isTrue();
        assertThat(firstCourse.has("price")).isTrue();
        assertThat(firstCourse.has("nextSessionDate")).isTrue();
    }

    @Test
    void testSearchByKeyword_ShouldReturnMatchingCourses() throws Exception {
        // When - Search for "math"
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/search?q=math", String.class);
        
        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        assertThat(jsonResponse.get("total").asInt()).isEqualTo(2);
        
        // Verify both math courses are returned
        JsonNode courses = jsonResponse.get("courses");
        List<String> titles = Arrays.asList(
            courses.get(0).get("title").asText(),
            courses.get(1).get("title").asText()
        );
        assertThat(titles).contains("Beginner Math Adventures", "Advanced Mathematics Challenge");
    }

    @Test
    void testFilterByCategory_ShouldReturnOnlyMatchingCategory() throws Exception {
        // When - Filter by Technology category
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/search?category=Technology", String.class);
        
        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        assertThat(jsonResponse.get("total").asInt()).isEqualTo(2);
        
        // Verify all returned courses are Technology category
        JsonNode courses = jsonResponse.get("courses");
        for (JsonNode course : courses) {
            assertThat(course.get("category").asText()).isEqualTo("Technology");
        }
    }

    @Test
    void testFilterByType_ShouldReturnOnlyMatchingType() throws Exception {
        // When - Filter by ONE_TIME type
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/search?type=ONE_TIME", String.class);
        
        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        assertThat(jsonResponse.get("total").asInt()).isEqualTo(2);
        
        // Verify all returned courses are ONE_TIME type
        JsonNode courses = jsonResponse.get("courses");
        for (JsonNode course : courses) {
            assertThat(course.get("type").asText()).isEqualTo("ONE_TIME");
        }
    }

    @Test
    void testAgeRangeFilter_ShouldReturnCoursesForTargetAge() throws Exception {
        // When - Search for courses suitable for age 10-12
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/search?minAge=10&maxAge=12", String.class);
        
        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        assertThat(jsonResponse.get("total").asInt()).isGreaterThan(0);
        
        // Verify age range overlap logic
        JsonNode courses = jsonResponse.get("courses");
        for (JsonNode course : courses) {
            int courseMinAge = course.get("minAge").asInt();
            int courseMaxAge = course.get("maxAge").asInt();
            
            // Course age range should overlap with requested range (10-12)
            assertThat(courseMaxAge).isGreaterThanOrEqualTo(10); // Course accepts students at least as old as 10
            assertThat(courseMinAge).isLessThanOrEqualTo(12);    // Course accepts students no older than 12
        }
    }

    @Test
    void testPriceRangeFilter_ShouldReturnCoursesInPriceRange() throws Exception {
        // When - Search for courses priced between $50-$150
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/search?minPrice=50&maxPrice=150", String.class);
        
        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        assertThat(jsonResponse.get("total").asInt()).isGreaterThan(0);
        
        // Verify all courses are within price range
        JsonNode courses = jsonResponse.get("courses");
        for (JsonNode course : courses) {
            double price = course.get("price").asDouble();
            assertThat(price).isBetween(50.0, 150.0);
        }
    }

    @Test
    void testSortByPriceAscending_ShouldReturnCoursesInPriceOrder() throws Exception {
        // When - Sort by price ascending
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/search?sort=priceAsc", String.class);
        
        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        JsonNode courses = jsonResponse.get("courses");
        
        // Verify courses are sorted by price (ascending)
        double previousPrice = 0.0;
        for (JsonNode course : courses) {
            double currentPrice = course.get("price").asDouble();
            assertThat(currentPrice).isGreaterThanOrEqualTo(previousPrice);
            previousPrice = currentPrice;
        }
        
        // Verify cheapest course is first
        assertThat(courses.get(0).get("price").asDouble()).isEqualTo(15.0);
        assertThat(courses.get(0).get("title").asText()).isEqualTo("Budget-Friendly Craft Time");
    }

    @Test
    void testSortByPriceDescending_ShouldReturnCoursesInReversePriceOrder() throws Exception {
        // When - Sort by price descending
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/search?sort=priceDesc", String.class);
        
        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        JsonNode courses = jsonResponse.get("courses");
        
        // Verify courses are sorted by price (descending)
        double previousPrice = Double.MAX_VALUE;
        for (JsonNode course : courses) {
            double currentPrice = course.get("price").asDouble();
            assertThat(currentPrice).isLessThanOrEqualTo(previousPrice);
            previousPrice = currentPrice;
        }
        
        // Verify most expensive course is first
        assertThat(courses.get(0).get("price").asDouble()).isEqualTo(250.0);
        assertThat(courses.get(0).get("title").asText()).isEqualTo("Premium Photography Course");
    }

    @Test
    void testSortByUpcoming_ShouldReturnCoursesInDateOrder() throws Exception {
        // When - Sort by upcoming (default)
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/search?sort=upcoming", String.class);
        
        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        JsonNode courses = jsonResponse.get("courses");
        
        // Verify courses are sorted by date (ascending)
        String previousDate = "";
        for (JsonNode course : courses) {
            String currentDate = course.get("nextSessionDate").asText();
            if (!previousDate.isEmpty()) {
                assertThat(currentDate).isGreaterThanOrEqualTo(previousDate);
            }
            previousDate = currentDate;
        }
        
        // Verify earliest course is first
        assertThat(courses.get(0).get("nextSessionDate").asText()).isEqualTo("2025-06-10T13:00:00Z");
    }

    @Test
    void testPagination_ShouldReturnCorrectPageAndMetadata() throws Exception {
        // When - Request second page with size 3
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/search?page=1&size=3", String.class);
        
        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        assertThat(jsonResponse.get("total").asInt()).isEqualTo(8);
        assertThat(jsonResponse.get("courses").size()).isEqualTo(3);
        
        // Verify pagination metadata
        JsonNode pagination = jsonResponse.get("pagination");
        assertThat(pagination.get("currentPage").asInt()).isEqualTo(1);
        assertThat(pagination.get("pageSize").asInt()).isEqualTo(3);
        assertThat(pagination.get("totalPages").asInt()).isEqualTo(3); // 8 courses / 3 per page = 3 pages
        assertThat(pagination.get("hasNext").asBoolean()).isTrue();
        assertThat(pagination.get("hasPrevious").asBoolean()).isTrue();
    }

    @Test
    void testCombinedFilters_ShouldApplyAllFiltersCorrectly() throws Exception {
        // When - Apply multiple filters: category=Technology, minAge=10, maxAge=18, sort=priceAsc
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/search?category=Technology&minAge=10&maxAge=18&sort=priceAsc", String.class);
        
        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        assertThat(jsonResponse.get("total").asInt()).isEqualTo(2);
        
        JsonNode courses = jsonResponse.get("courses");
        
        // Verify all filters are applied
        for (JsonNode course : courses) {
            assertThat(course.get("category").asText()).isEqualTo("Technology");
            assertThat(course.get("maxAge").asInt()).isGreaterThanOrEqualTo(10);
            assertThat(course.get("minAge").asInt()).isLessThanOrEqualTo(18);
        }
        
        // Verify price sorting (cheaper first)
        assertThat(courses.get(0).get("price").asDouble()).isLessThan(courses.get(1).get("price").asDouble());
    }

    @Test
    void testEmptyResult_ShouldReturnZeroCoursesWithCorrectStructure() throws Exception {
        // When - Search for non-existent category
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/search?category=NonExistentCategory", String.class);
        
        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        assertThat(jsonResponse.get("total").asInt()).isEqualTo(0);
        assertThat(jsonResponse.get("courses").size()).isEqualTo(0);
        
        // Verify pagination for empty result
        JsonNode pagination = jsonResponse.get("pagination");
        assertThat(pagination.get("currentPage").asInt()).isEqualTo(0);
        assertThat(pagination.get("totalPages").asInt()).isEqualTo(0);
        assertThat(pagination.get("hasNext").asBoolean()).isFalse();
        assertThat(pagination.get("hasPrevious").asBoolean()).isFalse();
    }

    @Test
    void testAutocompleteEndpoint_ShouldReturnRelevantSuggestions() throws Exception {
        // When - Request autocomplete for "art"
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/courses/autocomplete?query=art", String.class);
        
        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        
        String[] suggestions = objectMapper.readValue(response.getBody(), String[].class);
        assertThat(suggestions).hasSizeGreaterThan(0);
        
        // Verify suggestions contain "art" in the title
        for (String suggestion : suggestions) {
            assertThat(suggestion.toLowerCase()).contains("art");
        }
    }

    @Test
    void testApiDocumentation_ShouldReturnHelpInformation() throws Exception {
        // When - Request API help
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/search/help", String.class);
        
        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        assertThat(jsonResponse.has("endpoint")).isTrue();
        assertThat(jsonResponse.has("description")).isTrue();
        assertThat(jsonResponse.has("parameters")).isTrue();
        assertThat(jsonResponse.has("examples")).isTrue();
        
        // Verify endpoint documentation
        assertThat(jsonResponse.get("endpoint").asText()).isEqualTo("GET /api/search");
    }
}
