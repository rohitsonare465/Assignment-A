# Course Search Application

A Spring Boot application that demonstrates Elasticsearch integration for course search functionality.

## Features

- **Assignment A**: Course indexing and search with filters, pagination, and sorting
- **Assignment B (Bonus)**: Autocomplete suggestions and fuzzy matching

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Da### 🧪 Quick Start Verification

1. **Start services**: `docker compose up -d`
2. **Run application**: `./mvnw spring-boot:run`
3. **Verify data loading**: Check logs for "Successfully loaded 50 sample courses"
4. **Test search**: `curl "http://localhost:8080/api/courses/search?category=Science"`
5. **Test autocomplete**: `curl "http://localhost:8080/api/courses/autocomplete?query=math"`

## 🧪 Testing & Verification

### API Testing with curl

The application exposes a standardized search endpoint at `/api/search` with comprehensive filtering, sorting, and pagination capabilities.

#### Basic Search Examples

```bash
# 1. Search by keyword (searches title and description)
curl "http://localhost:8080/api/search?q=math"
# Expected: Returns 3 courses with "math" in title/description
# Response includes: Math Adventures, Advanced Mathematics, Math Olympiad

# 2. Get API documentation
curl "http://localhost:8080/api/search/help"
# Expected: Complete parameter reference and examples

# 3. Search all courses (no filters)
curl "http://localhost:8080/api/search"
# Expected: Returns first 10 courses (total: 51) sorted by upcoming date
```

#### Filtering Examples

```bash
# 4. Filter by category
curl "http://localhost:8080/api/search?category=Science"
# Expected: Returns ~8 science courses
# Response includes: Science Spectacular, Chemistry Lab, Physics Fun, etc.

# 5. Filter by course type
curl "http://localhost:8080/api/search?type=ONE_TIME"
# Expected: Returns 10 one-time event courses
# Response includes workshops, shows, and special events

# 6. Age range filtering
curl "http://localhost:8080/api/search?minAge=10&maxAge=15"
# Expected: Returns courses suitable for ages 10-15
# Filters courses where age range overlaps with 10-15

# 7. Price range filtering
curl "http://localhost:8080/api/search?minPrice=50&maxPrice=100"
# Expected: Returns courses priced between $50-$100
# Response excludes very cheap (<$50) and expensive (>$100) courses

# 8. Combined filters
curl "http://localhost:8080/api/search?category=Technology&type=CLUB&minAge=12&maxAge=17"
# Expected: Returns technology clubs for teens
# Response: Robotics Engineering Club, Video Game Design, etc.
```

#### Sorting Examples

```bash
# 9. Sort by price (ascending)
curl "http://localhost:8080/api/search?sort=priceAsc&size=5"
# Expected: Returns 5 cheapest courses
# First course should be "Magic Show Spectacular" ($15)

# 10. Sort by price (descending)
curl "http://localhost:8080/api/search?sort=priceDesc&size=5"
# Expected: Returns 5 most expensive courses
# First course should be "Robotics Engineering Club" ($180)

# 11. Sort by upcoming date (default)
curl "http://localhost:8080/api/search?sort=upcoming&size=5"
# Expected: Returns 5 courses with earliest start dates
# Courses sorted chronologically by nextSessionDate
```

#### Pagination Examples

```bash
# 12. First page (default)
curl "http://localhost:8080/api/search?page=0&size=5"
# Expected: Returns first 5 courses with pagination metadata
# Response: currentPage=0, hasNext=true, hasPrevious=false

# 13. Second page
curl "http://localhost:8080/api/search?page=1&size=5"
# Expected: Returns courses 6-10 with pagination metadata
# Response: currentPage=1, hasNext=true, hasPrevious=true

# 14. Large page size
curl "http://localhost:8080/api/search?size=20"
# Expected: Returns 20 courses (or all if fewer than 20 match)
# Response: Shows impact on totalPages calculation
```

#### Advanced Filtering Examples

```bash
# 15. Date filtering (courses starting after specific date)
curl "http://localhost:8080/api/search?startDate=2025-07-01&sort=upcoming"
# Expected: Returns courses starting July 1st, 2025 or later
# Response excludes June 2025 courses

# 16. Complex multi-filter query
curl "http://localhost:8080/api/search?q=programming&category=Technology&minAge=8&maxAge=16&minPrice=80&maxPrice=150&sort=priceAsc"
# Expected: Returns programming/technology courses for kids/teens in price range
# Response: Filtered and sorted results with detailed pagination

# 17. Empty result set
curl "http://localhost:8080/api/search?category=NonExistent"
# Expected: Returns empty results
# Response: total=0, courses=[], proper pagination for empty set
```

### Expected Response Format

All `/api/search` responses follow this structure:

```json
{
  "total": 51,
  "courses": [
    {
      "id": "1",
      "title": "Math Adventures for Little Explorers",
      "category": "Math",
      "price": 85.0,
      "nextSessionDate": "2025-06-10T15:00:00Z",
      "description": "Fun and interactive math activities...",
      "type": "COURSE",
      "gradeRange": "K-2nd",
      "minAge": 5,
      "maxAge": 7
    }
  ],
  "pagination": {
    "currentPage": 0,
    "pageSize": 10,
    "totalPages": 6,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

### Expected Behaviors

#### Sorting Behavior
- **`upcoming`** (default): Sorts by `nextSessionDate` ascending (earliest first)
- **`priceAsc`**: Sorts by `price` ascending (cheapest first)
- **`priceDesc`**: Sorts by `price` descending (most expensive first)

#### Filtering Behavior
- **Text search** (`q`): Searches both `title` and `description` fields
- **Age filtering**: Finds courses where age ranges overlap with requested range
- **Price filtering**: Exact range matching on course price
- **Category/Type**: Exact string matching (case-sensitive)
- **Date filtering**: Includes courses starting on or after the specified date

#### Pagination Behavior
- **`page`**: Zero-based indexing (0 = first page)
- **`size`**: Number of results per page (default: 10, max recommended: 100)
- **Metadata**: Includes navigation helpers (`hasNext`, `hasPrevious`)

### Quick Verification Commands

```bash
# Verify total course count
curl -s "http://localhost:8080/api/search" | jq '.total'
# Expected: 51

# Verify price sorting works
curl -s "http://localhost:8080/api/search?sort=priceAsc&size=3" | jq '.courses[].price'
# Expected: [15.0, 25.0, 30.0] (ascending order)

# Verify category filtering works
curl -s "http://localhost:8080/api/search?category=Math" | jq '.total'
# Expected: 4 (Math Adventures, Advanced Mathematics, Geometry, Math Olympiad)

# Verify pagination calculation
curl -s "http://localhost:8080/api/search?size=5" | jq '.pagination.totalPages'
# Expected: 11 (51 total ÷ 5 per page = 11 pages)

# Verify autocomplete works
curl -s "http://localhost:8080/api/courses/autocomplete?query=art" | jq 'length'
# Expected: 4 (Young Artists Club, Origami Art Workshop, Martial Arts, Knitting and Fiber Arts)
```

### Automated Testing Script

Run the comprehensive test suite that verifies all API functionality:

```bash
# Make the script executable and run all tests
chmod +x test-api.sh
./test-api.sh
```

This script tests:
- ✅ Health endpoints and API documentation
- ✅ Basic search functionality (keyword, category, type filters)
- ✅ Age range and price range filtering  
- ✅ All sorting options (upcoming, priceAsc, priceDesc)
- ✅ Pagination with different page sizes
- ✅ Complex multi-parameter queries
- ✅ Edge cases (empty results, non-existent categories)
- ✅ Autocomplete functionality
- ✅ Legacy endpoint compatibility
- ✅ Performance measurement

**Expected Test Results:**
- All 19 test cases should pass ✅
- Response times should be under 100ms for most queries
- Total course count: 51 courses across 10 categories
- Autocomplete should return 3-4 suggestions for "art" and "math"

### Data Distribution Verification

```bash
# Check course distribution by category
curl -s "http://localhost:8080/api/search?category=Science" | jq '.total'     # Expected: 10
curl -s "http://localhost:8080/api/search?category=Technology" | jq '.total'  # Expected: 4  
curl -s "http://localhost:8080/api/search?category=Math" | jq '.total'        # Expected: 4
curl -s "http://localhost:8080/api/search?category=Art" | jq '.total'         # Expected: 3

# Check course distribution by type
curl -s "http://localhost:8080/api/search?type=COURSE" | jq '.total'          # Expected: 31
curl -s "http://localhost:8080/api/search?type=CLUB" | jq '.total'            # Expected: 10
curl -s "http://localhost:8080/api/search?type=ONE_TIME" | jq '.total'        # Expected: 10

# Verify price range distribution
curl -s "http://localhost:8080/api/search?maxPrice=50" | jq '.total'          # Expected: 8 (budget courses)
curl -s "http://localhost:8080/api/search?minPrice=150" | jq '.total'         # Expected: 3 (premium courses)
```

---

## 📋 Complete cURL Testing Examples

This section provides exact cURL commands for testing every aspect of the `/api/search` endpoint with expected responses and behaviors.

### 1. Basic API Endpoints

#### Health Check and Documentation
```bash
# Check application health
curl "http://localhost:8080/actuator/health"
# Expected: {"status":"UP","components":{...}}

# Get API documentation
curl "http://localhost:8080/api/search/help"
# Expected: Complete parameter reference and usage examples
```

### 2. Text Search Examples

#### Basic Text Search
```bash
# Search for courses containing "math"
curl "http://localhost:8080/api/search?q=math"
# Expected: 3 courses (Math Adventures, Advanced Mathematics, Math Olympiad)
# Response includes title and description matches

# Search for courses containing "programming" 
curl "http://localhost:8080/api/search?q=programming"
# Expected: 2-3 courses with programming-related content
# Searches both title and description fields
```

#### Expected Text Search Behavior:
- ✅ Searches both `title` and `description` fields
- ✅ Case-insensitive matching
- ✅ Partial word matching supported
- ✅ Returns courses sorted by relevance and date

### 3. Category Filtering Examples

#### Filter by Course Category
```bash
# Get all Science courses
curl "http://localhost:8080/api/search?category=Science"
# Expected: 10 courses (Science Spectacular, Dinosaur Discovery, Chemistry Lab, etc.)

# Get all Technology courses  
curl "http://localhost:8080/api/search?category=Technology"
# Expected: 4 courses (Robotics, Coding with Scratch, 3D Printing, Video Game Design)

# Get all Math courses
curl "http://localhost:8080/api/search?category=Math"
# Expected: 4 courses (Math Adventures, Advanced Math, Geometry, Math Olympiad)
```

#### Expected Category Behavior:
- ✅ Exact string matching (case-sensitive)
- ✅ Available categories: Science, Technology, Math, Art, Music, Sports, Language Arts, Life Skills, Logic, Entertainment
- ✅ Non-existent categories return empty results

### 4. Course Type Filtering Examples

#### Filter by Course Type
```bash
# Get all one-time events
curl "http://localhost:8080/api/search?type=ONE_TIME"
# Expected: 10 courses (workshops, shows, special events)

# Get all ongoing courses
curl "http://localhost:8080/api/search?type=COURSE"
# Expected: 31 regular courses

# Get all clubs
curl "http://localhost:8080/api/search?type=CLUB"
# Expected: 10 ongoing clubs and activities
```

#### Expected Type Behavior:
- ✅ Valid types: `ONE_TIME`, `COURSE`, `CLUB`
- ✅ Exact matching required
- ✅ Invalid types return empty results

### 5. Age Range Filtering Examples

#### Age-Based Filtering
```bash
# Courses suitable for ages 10-15
curl "http://localhost:8080/api/search?minAge=10&maxAge=15"
# Expected: 48 courses where age ranges overlap with 10-15

# Courses for very young children (ages 5-8)
curl "http://localhost:8080/api/search?minAge=5&maxAge=8"
# Expected: ~15 courses suitable for early elementary

# Courses for teenagers (ages 14-17)
curl "http://localhost:8080/api/search?minAge=14&maxAge=17"
# Expected: ~20 courses for high school age students
```

#### Expected Age Range Behavior:
- ✅ **Overlap Logic**: Returns courses where the course age range overlaps with requested range
- ✅ Example: Course for ages 8-12 appears in search for ages 10-15 (overlap: 10-12)
- ✅ Both `minAge` and `maxAge` are optional (can use just one)

### 6. Price Range Filtering Examples

#### Price-Based Filtering
```bash
# Budget courses ($0-$50)
curl "http://localhost:8080/api/search?maxPrice=50"
# Expected: 8 courses (Magic Show $15, Science Workshop $25, etc.)

# Mid-range courses ($50-$100)
curl "http://localhost:8080/api/search?minPrice=50&maxPrice=100"
# Expected: 20 courses in this price range

# Premium courses ($150+)
curl "http://localhost:8080/api/search?minPrice=150"
# Expected: 3 courses (Robotics $180, 3D Printing $160, Video Game Design $150)
```

#### Expected Price Behavior:
- ✅ **Exact Range**: Course price must fall within specified range
- ✅ Both `minPrice` and `maxPrice` are optional
- ✅ Decimal values supported (e.g., 49.99)

### 7. Date Filtering Examples

#### Date-Based Filtering
```bash
# Courses starting in July 2025 or later
curl "http://localhost:8080/api/search?startDate=2025-07-01"
# Expected: ~40 courses starting July 1st or later

# Courses starting after September 1, 2025
curl "http://localhost:8080/api/search?startDate=2025-09-01" 
# Expected: ~15 courses in September and later
```

#### Expected Date Behavior:
- ✅ **ISO-8601 Format**: Use format `YYYY-MM-DD` or `YYYY-MM-DDTHH:MM:SSZ`
- ✅ **Inclusive Filter**: Includes courses starting exactly on the specified date
- ✅ **Future Focus**: Filters courses starting on or after the date

### 8. Sorting Examples

#### Sort by Price (Ascending)
```bash
curl "http://localhost:8080/api/search?sort=priceAsc&size=5"
# Expected Response:
# 1. Magic Show Spectacular - $15.00
# 2. Science Spectacular Workshop - $25.00  
# 3. Astronomy Night - $30.00
# 4. Musical Theater Performance - $35.00
# 5. Pet Care Workshop - $35.00
```

#### Sort by Price (Descending)
```bash
curl "http://localhost:8080/api/search?sort=priceDesc&size=5"
# Expected Response:
# 1. Robotics Engineering Club - $180.00
# 2. 3D Printing Workshop - $160.00
# 3. Video Game Design - $150.00
# 4. Chemistry Lab Experiments - $145.00
# 5. Soccer Skills Development - $140.00
```

#### Sort by Upcoming Date (Default)
```bash
curl "http://localhost:8080/api/search?sort=upcoming&size=5"
# Expected Response (earliest dates first):
# 1. Math Adventures - 2025-06-10T15:00:00Z
# 2. Young Artists Club - 2025-06-12T16:30:00Z
# 3. Soccer Skills Development - 2025-06-14T10:00:00Z
# 4. Science Spectacular - 2025-06-15T14:00:00Z
# 5. Robotics Engineering - 2025-06-16T16:00:00Z
```

#### Expected Sorting Behavior:
- ✅ **upcoming** (default): Sort by `nextSessionDate` ascending
- ✅ **priceAsc**: Sort by `price` ascending (cheapest first)
- ✅ **priceDesc**: Sort by `price` descending (most expensive first)

### 9. Pagination Examples

#### Basic Pagination
```bash
# First page (5 items)
curl "http://localhost:8080/api/search?page=0&size=5"
# Expected: 5 courses, pagination shows currentPage=0, hasNext=true, hasPrevious=false

# Second page (5 items)
curl "http://localhost:8080/api/search?page=1&size=5"
# Expected: 5 courses, pagination shows currentPage=1, hasNext=true, hasPrevious=true

# Large page size
curl "http://localhost:8080/api/search?page=0&size=20"
# Expected: 20 courses, pagination shows totalPages=3 (51 total / 20 per page)
```

#### Expected Pagination Behavior:
- ✅ **Zero-based indexing**: First page is `page=0`
- ✅ **Default size**: 10 items per page if not specified
- ✅ **Metadata included**: `currentPage`, `pageSize`, `totalPages`, `hasNext`, `hasPrevious`
- ✅ **Total count**: Always shows total matching courses regardless of page

### 10. Complex Multi-Filter Examples

#### Technology Courses for Teens, Sorted by Price
```bash
curl "http://localhost:8080/api/search?category=Technology&minAge=10&maxAge=18&sort=priceAsc"
# Expected: 4 courses
# 1. Coding with Scratch - $110 (ages 8-12, overlaps with 10-18)
# 2. Video Game Design - $150 (ages 11-17)
# 3. 3D Printing Workshop - $160 (ages 11-17)  
# 4. Robotics Engineering Club - $180 (ages 11-17)
```

#### Budget Science Events for Elementary Students
```bash
curl "http://localhost:8080/api/search?category=Science&type=ONE_TIME&maxPrice=50&minAge=5&maxAge=12"
# Expected: 3 courses
# 1. Science Spectacular Workshop - $25 (ages 8-13)
# 2. Astronomy Night - $30 (ages 9-17) 
# 3. Dinosaur Discovery Day - $40 (ages 5-9)
```

#### Programming-Related Courses with Text Search
```bash
curl "http://localhost:8080/api/search?q=programming&minPrice=80&maxPrice=200&sort=priceAsc"
# Expected: 3 courses containing "programming" in title/description
# Results depend on text matching in sample data
```

### 11. Edge Cases and Error Scenarios

#### Empty Result Sets
```bash
# Non-existent category
curl "http://localhost:8080/api/search?category=NonExistentCategory"
# Expected: {"total":0,"courses":[],"pagination":{...}}

# Impossible age range
curl "http://localhost:8080/api/search?minAge=20&maxAge=25"
# Expected: {"total":0,"courses":[],"pagination":{...}}

# Very high price filter
curl "http://localhost:8080/api/search?minPrice=500"
# Expected: {"total":0,"courses":[],"pagination":{...}}
```

#### Invalid Parameters
```bash
# Invalid sort parameter (falls back to default)
curl "http://localhost:8080/api/search?sort=invalidSort"
# Expected: Results sorted by upcoming date (default behavior)

# Invalid page number (returns empty but valid response)
curl "http://localhost:8080/api/search?page=999"
# Expected: {"total":51,"courses":[],"pagination":{...}}
```

### 12. Response Format Validation

Every `/api/search` response follows this exact structure:

```json
{
  "total": 51,
  "courses": [
    {
      "id": "1",
      "title": "Math Adventures for Little Explorers",
      "category": "Math", 
      "price": 85.0,
      "nextSessionDate": "2025-06-10T15:00:00Z",
      "description": "Fun and interactive math activities...",
      "type": "COURSE",
      "gradeRange": "K-2nd",
      "minAge": 5,
      "maxAge": 7
    }
  ],
  "pagination": {
    "currentPage": 0,
    "pageSize": 10,
    "totalPages": 6,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

#### Required Response Fields:
- ✅ `total`: Total number of matching courses
- ✅ `courses`: Array of course objects with all required fields
- ✅ `pagination`: Complete pagination metadata

### 13. Autocomplete Testing

#### Autocomplete Suggestions
```bash
# Get suggestions for "art"
curl "http://localhost:8080/api/courses/autocomplete?query=art"
# Expected: ["Young Artists Club","Origami Art Workshop","Martial Arts Introduction","Knitting and Fiber Arts"]

# Get suggestions for "math"  
curl "http://localhost:8080/api/courses/autocomplete?query=math"
# Expected: ["Math Adventures for Little Explorers","Advanced Mathematics Challenge","Math Olympiad Preparation"]

# Get suggestions for "science"
curl "http://localhost:8080/api/courses/autocomplete?query=science"
# Expected: Multiple science-related course titles
```

#### Expected Autocomplete Behavior:
- ✅ Returns array of course titles (not full course objects)
- ✅ Matches based on course title
- ✅ Limited to relevant suggestions (typically 3-5 results)
- ✅ Fast response time for real-time suggestions

---
````markdown
# Course Search Application

A Spring Boot application that demonstrates Elasticsearch integration for course search functionality.

## Features

- **Assignment A**: Course indexing and search with filters, pagination, and sorting
- **Assignment B (Bonus)**: Autocomplete suggestions and fuzzy matching

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Da### 🧪 Quick Start Verification

1. **Start services**: `docker compose up -d`
2. **Run application**: `./mvnw spring-boot:run`
3. **Verify data loading**: Check logs for "Successfully loaded 50 sample courses"
4. **Test search**: `curl "http://localhost:8080/api/courses/search?category=Science"`
5. **Test autocomplete**: `curl "http://localhost:8080/api/courses/autocomplete?query=math"`

## 🧪 Testing & Verification

### API Testing with curl

The application exposes a standardized search endpoint at `/api/search` with comprehensive filtering, sorting, and pagination capabilities.

#### Basic Search Examples

```bash
# 1. Search by keyword (searches title and description)
curl "http://localhost:8080/api/search?q=math"
# Expected: Returns 3 courses with "math" in title/description
# Response includes: Math Adventures, Advanced Mathematics, Math Olympiad

# 2. Get API documentation
curl "http://localhost:8080/api/search/help"
# Expected: Complete parameter reference and examples

# 3. Search all courses (no filters)
curl "http://localhost:8080/api/search"
# Expected: Returns first 10 courses (total: 51) sorted by upcoming date
```

#### Filtering Examples

```bash
# 4. Filter by category
curl "http://localhost:8080/api/search?category=Science"
# Expected: Returns ~8 science courses
# Response includes: Science Spectacular, Chemistry Lab, Physics Fun, etc.

# 5. Filter by course type
curl "http://localhost:8080/api/search?type=ONE_TIME"
# Expected: Returns 10 one-time event courses
# Response includes workshops, shows, and special events

# 6. Age range filtering
curl "http://localhost:8080/api/search?minAge=10&maxAge=15"
# Expected: Returns courses suitable for ages 10-15
# Filters courses where age range overlaps with 10-15

# 7. Price range filtering
curl "http://localhost:8080/api/search?minPrice=50&maxPrice=100"
# Expected: Returns courses priced between $50-$100
# Response excludes very cheap (<$50) and expensive (>$100) courses

# 8. Combined filters
curl "http://localhost:8080/api/search?category=Technology&type=CLUB&minAge=12&maxAge=17"
# Expected: Returns technology clubs for teens
# Response: Robotics Engineering Club, Video Game Design, etc.
```

#### Sorting Examples

```bash
# 9. Sort by price (ascending)
curl "http://localhost:8080/api/search?sort=priceAsc&size=5"
# Expected: Returns 5 cheapest courses
# First course should be "Magic Show Spectacular" ($15)

# 10. Sort by price (descending)
curl "http://localhost:8080/api/search?sort=priceDesc&size=5"
# Expected: Returns 5 most expensive courses
# First course should be "Robotics Engineering Club" ($180)

# 11. Sort by upcoming date (default)
curl "http://localhost:8080/api/search?sort=upcoming&size=5"
# Expected: Returns 5 courses with earliest start dates
# Courses sorted chronologically by nextSessionDate
```

#### Pagination Examples

```bash
# 12. First page (default)
curl "http://localhost:8080/api/search?page=0&size=5"
# Expected: Returns first 5 courses with pagination metadata
# Response: currentPage=0, hasNext=true, hasPrevious=false

# 13. Second page
curl "http://localhost:8080/api/search?page=1&size=5"
# Expected: Returns courses 6-10 with pagination metadata
# Response: currentPage=1, hasNext=true, hasPrevious=true

# 14. Large page size
curl "http://localhost:8080/api/search?size=20"
# Expected: Returns 20 courses (or all if fewer than 20 match)
# Response: Shows impact on totalPages calculation
```

#### Advanced Filtering Examples

```bash
# 15. Date filtering (courses starting after specific date)
curl "http://localhost:8080/api/search?startDate=2025-07-01&sort=upcoming"
# Expected: Returns courses starting July 1st, 2025 or later
# Response excludes June 2025 courses

# 16. Complex multi-filter query
curl "http://localhost:8080/api/search?q=programming&category=Technology&minAge=8&maxAge=16&minPrice=80&maxPrice=150&sort=priceAsc"
# Expected: Returns programming/technology courses for kids/teens in price range
# Response: Filtered and sorted results with detailed pagination

# 17. Empty result set
curl "http://localhost:8080/api/search?category=NonExistent"
# Expected: Returns empty results
# Response: total=0, courses=[], proper pagination for empty set
```

### Expected Response Format

All `/api/search` responses follow this structure:

```json
{
  "total": 51,
  "courses": [
    {
      "id": "1",
      "title": "Math Adventures for Little Explorers",
      "category": "Math",
      "price": 85.0,
      "nextSessionDate": "2025-06-10T15:00:00Z",
      "description": "Fun and interactive math activities...",
      "type": "COURSE",
      "gradeRange": "K-2nd",
      "minAge": 5,
      "maxAge": 7
    }
  ],
  "pagination": {
    "currentPage": 0,
    "pageSize": 10,
    "totalPages": 6,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

### Expected Behaviors

#### Sorting Behavior
- **`upcoming`** (default): Sorts by `nextSessionDate` ascending (earliest first)
- **`priceAsc`**: Sorts by `price` ascending (cheapest first)
- **`priceDesc`**: Sorts by `price` descending (most expensive first)

#### Filtering Behavior
- **Text search** (`q`): Searches both `title` and `description` fields
- **Age filtering**: Finds courses where age ranges overlap with requested range
- **Price filtering**: Exact range matching on course price
- **Category/Type**: Exact string matching (case-sensitive)
- **Date filtering**: Includes courses starting on or after the specified date

#### Pagination Behavior
- **`page`**: Zero-based indexing (0 = first page)
- **`size`**: Number of results per page (default: 10, max recommended: 100)
- **Metadata**: Includes navigation helpers (`hasNext`, `hasPrevious`)

### Quick Verification Commands

```bash
# Verify total course count
curl -s "http://localhost:8080/api/search" | jq '.total'
# Expected: 51

# Verify price sorting works
curl -s "http://localhost:8080/api/search?sort=priceAsc&size=3" | jq '.courses[].price'
# Expected: [15.0, 25.0, 30.0] (ascending order)

# Verify category filtering works
curl -s "http://localhost:8080/api/search?category=Math" | jq '.total'
# Expected: 4 (Math Adventures, Advanced Mathematics, Geometry, Math Olympiad)

# Verify pagination calculation
curl -s "http://localhost:8080/api/search?size=5" | jq '.pagination.totalPages'
# Expected: 11 (51 total ÷ 5 per page = 11 pages)

# Verify autocomplete works
curl -s "http://localhost:8080/api/courses/autocomplete?query=art" | jq 'length'
# Expected: 4 (Young Artists Club, Origami Art Workshop, Martial Arts, Knitting and Fiber Arts)
```

### Automated Testing Script

Run the comprehensive test suite that verifies all API functionality:

```bash
# Make the script executable and run all tests
chmod +x test-api.sh
./test-api.sh
```

This script tests:
- ✅ Health endpoints and API documentation
- ✅ Basic search functionality (keyword, category, type filters)
- ✅ Age range and price range filtering  
- ✅ All sorting options (upcoming, priceAsc, priceDesc)
- ✅ Pagination with different page sizes
- ✅ Complex multi-parameter queries
- ✅ Edge cases (empty results, non-existent categories)
- ✅ Autocomplete functionality
- ✅ Legacy endpoint compatibility
- ✅ Performance measurement

**Expected Test Results:**
- All 19 test cases should pass ✅
- Response times should be under 100ms for most queries
- Total course count: 51 courses across 10 categories
- Autocomplete should return 3-4 suggestions for "art" and "math"

### Data Distribution Verification

```bash
# Check course distribution by category
curl -s "http://localhost:8080/api/search?category=Science" | jq '.total'     # Expected: 10
curl -s "http://localhost:8080/api/search?category=Technology" | jq '.total'  # Expected: 4  
curl -s "http://localhost:8080/api/search?category=Math" | jq '.total'        # Expected: 4
curl -s "http://localhost:8080/api/search?category=Art" | jq '.total'         # Expected: 3

# Check course distribution by type
curl -s "http://localhost:8080/api/search?type=COURSE" | jq '.total'          # Expected: 31
curl -s "http://localhost:8080/api/search?type=CLUB" | jq '.total'            # Expected: 10
curl -s "http://localhost:8080/api/search?type=ONE_TIME" | jq '.total'        # Expected: 10

# Verify price range distribution
curl -s "http://localhost:8080/api/search?maxPrice=50" | jq '.total'          # Expected: 8 (budget courses)
curl -s "http://localhost:8080/api/search?minPrice=150" | jq '.total'         # Expected: 3 (premium courses)
```

---

## 📋 Complete cURL Testing Examples

This section provides exact cURL commands for testing every aspect of the `/api/search` endpoint with expected responses and behaviors.

### 1. Basic API Endpoints

#### Health Check and Documentation
```bash
# Check application health
curl "http://localhost:8080/actuator/health"
# Expected: {"status":"UP","components":{...}}

# Get API documentation
curl "http://localhost:8080/api/search/help"
# Expected: Complete parameter reference and usage examples
```

### 2. Text Search Examples

#### Basic Text Search
```bash
# Search for courses containing "math"
curl "http://localhost:8080/api/search?q=math"
# Expected: 3 courses (Math Adventures, Advanced Mathematics, Math Olympiad)
# Response includes title and description matches

# Search for courses containing "programming" 
curl "http://localhost:8080/api/search?q=programming"
# Expected: 2-3 courses with programming-related content
# Searches both title and description fields
```

#### Expected Text Search Behavior:
- ✅ Searches both `title` and `description` fields
- ✅ Case-insensitive matching
- ✅ Partial word matching supported
- ✅ Returns courses sorted by relevance and date

### 3. Category Filtering Examples

#### Filter by Course Category
```bash
# Get all Science courses
curl "http://localhost:8080/api/search?category=Science"
# Expected: 10 courses (Science Spectacular, Dinosaur Discovery, Chemistry Lab, etc.)

# Get all Technology courses  
curl "http://localhost:8080/api/search?category=Technology"
# Expected: 4 courses (Robotics, Coding with Scratch, 3D Printing, Video Game Design)

# Get all Math courses
curl "http://localhost:8080/api/search?category=Math"
# Expected: 4 courses (Math Adventures, Advanced Math, Geometry, Math Olympiad)
```

#### Expected Category Behavior:
- ✅ Exact string matching (case-sensitive)
- ✅ Available categories: Science, Technology, Math, Art, Music, Sports, Language Arts, Life Skills, Logic, Entertainment
- ✅ Non-existent categories return empty results

### 4. Course Type Filtering Examples

#### Filter by Course Type
```bash
# Get all one-time events
curl "http://localhost:8080/api/search?type=ONE_TIME"
# Expected: 10 courses (workshops, shows, special events)

# Get all ongoing courses
curl "http://localhost:8080/api/search?type=COURSE"
# Expected: 31 regular courses

# Get all clubs
curl "http://localhost:8080/api/search?type=CLUB"
# Expected: 10 ongoing clubs and activities
```

#### Expected Type Behavior:
- ✅ Valid types: `ONE_TIME`, `COURSE`, `CLUB`
- ✅ Exact matching required
- ✅ Invalid types return empty results

### 5. Age Range Filtering Examples

#### Age-Based Filtering
```bash
# Courses suitable for ages 10-15
curl "http://localhost:8080/api/search?minAge=10&maxAge=15"
# Expected: 48 courses where age ranges overlap with 10-15

# Courses for very young children (ages 5-8)
curl "http://localhost:8080/api/search?minAge=5&maxAge=8"
# Expected: ~15 courses suitable for early elementary

# Courses for teenagers (ages 14-17)
curl "http://localhost:8080/api/search?minAge=14&maxAge=17"
# Expected: ~20 courses for high school age students
```

#### Expected Age Range Behavior:
- ✅ **Overlap Logic**: Returns courses where the course age range overlaps with requested range
- ✅ Example: Course for ages 8-12 appears in search for ages 10-15 (overlap: 10-12)
- ✅ Both `minAge` and `maxAge` are optional (can use just one)

### 6. Price Range Filtering Examples

#### Price-Based Filtering
```bash
# Budget courses ($0-$50)
curl "http://localhost:8080/api/search?maxPrice=50"
# Expected: 8 courses (Magic Show $15, Science Workshop $25, etc.)

# Mid-range courses ($50-$100)
curl "http://localhost:8080/api/search?minPrice=50&maxPrice=100"
# Expected: 20 courses in this price range

# Premium courses ($150+)
curl "http://localhost:8080/api/search?minPrice=150"
# Expected: 3 courses (Robotics $180, 3D Printing $160, Video Game Design $150)
```

#### Expected Price Behavior:
- ✅ **Exact Range**: Course price must fall within specified range
- ✅ Both `minPrice` and `maxPrice` are optional
- ✅ Decimal values supported (e.g., 49.99)

### 7. Date Filtering Examples

#### Date-Based Filtering
```bash
# Courses starting in July 2025 or later
curl "http://localhost:8080/api/search?startDate=2025-07-01"
# Expected: ~40 courses starting July 1st or later

# Courses starting after September 1, 2025
curl "http://localhost:8080/api/search?startDate=2025-09-01" 
# Expected: ~15 courses in September and later
```

#### Expected Date Behavior:
- ✅ **ISO-8601 Format**: Use format `YYYY-MM-DD` or `YYYY-MM-DDTHH:MM:SSZ`
- ✅ **Inclusive Filter**: Includes courses starting exactly on the specified date
- ✅ **Future Focus**: Filters courses starting on or after the date

### 8. Sorting Examples

#### Sort by Price (Ascending)
```bash
curl "http://localhost:8080/api/search?sort=priceAsc&size=5"
# Expected Response:
# 1. Magic Show Spectacular - $15.00
# 2. Science Spectacular Workshop - $25.00  
# 3. Astronomy Night - $30.00
# 4. Musical Theater Performance - $35.00
# 5. Pet Care Workshop - $35.00
```

#### Sort by Price (Descending)
```bash
curl "http://localhost:8080/api/search?sort=priceDesc&size=5"
# Expected Response:
# 1. Robotics Engineering Club - $180.00
# 2. 3D Printing Workshop - $160.00
# 3. Video Game Design - $150.00
# 4. Chemistry Lab Experiments - $145.00
# 5. Soccer Skills Development - $140.00
```

#### Sort by Upcoming Date (Default)
```bash
curl "http://localhost:8080/api/search?sort=upcoming&size=5"
# Expected Response (earliest dates first):
# 1. Math Adventures - 2025-06-10T15:00:00Z
# 2. Young Artists Club - 2025-06-12T16:30:00Z
# 3. Soccer Skills Development - 2025-06-14T10:00:00Z
# 4. Science Spectacular - 2025-06-15T14:00:00Z
# 5. Robotics Engineering - 2025-06-16T16:00:00Z
```

#### Expected Sorting Behavior:
- ✅ **upcoming** (default): Sort by `nextSessionDate` ascending
- ✅ **priceAsc**: Sort by `price` ascending (cheapest first)
- ✅ **priceDesc**: Sort by `price` descending (most expensive first)

### 9. Pagination Examples

#### Basic Pagination
```bash
# First page (5 items)
curl "http://localhost:8080/api/search?page=0&size=5"
# Expected: 5 courses, pagination shows currentPage=0, hasNext=true, hasPrevious=false

# Second page (5 items)
curl "http://localhost:8080/api/search?page=1&size=5"
# Expected: 5 courses, pagination shows currentPage=1, hasNext=true, hasPrevious=true

# Large page size
curl "http://localhost:8080/api/search?page=0&size=20"
# Expected: 20 courses, pagination shows totalPages=3 (51 total / 20 per page)
```

#### Expected Pagination Behavior:
- ✅ **Zero-based indexing**: First page is `page=0`
- ✅ **Default size**: 10 items per page if not specified
- ✅ **Metadata included**: `currentPage`, `pageSize`, `totalPages`, `hasNext`, `hasPrevious`
- ✅ **Total count**: Always shows total matching courses regardless of page

### 10. Complex Multi-Filter Examples

#### Technology Courses for Teens, Sorted by Price
```bash
curl "http://localhost:8080/api/search?category=Technology&minAge=10&maxAge=18&sort=priceAsc"
# Expected: 4 courses
# 1. Coding with Scratch - $110 (ages 8-12, overlaps with 10-18)
# 2. Video Game Design - $150 (ages 11-17)
# 3. 3D Printing Workshop - $160 (ages 11-17)  
# 4. Robotics Engineering Club - $180 (ages 11-17)
```

#### Budget Science Events for Elementary Students
```bash
curl "http://localhost:8080/api/search?category=Science&type=ONE_TIME&maxPrice=50&minAge=5&maxAge=12"
# Expected: 3 courses
# 1. Science Spectacular Workshop - $25 (ages 8-13)
# 2. Astronomy Night - $30 (ages 9-17) 
# 3. Dinosaur Discovery Day - $40 (ages 5-9)
```

#### Programming-Related Courses with Text Search
```bash
curl "http://localhost:8080/api/search?q=programming&minPrice=80&maxPrice=200&sort=priceAsc"
# Expected: 3 courses containing "programming" in title/description
# Results depend on text matching in sample data
```

### 11. Edge Cases and Error Scenarios

#### Empty Result Sets
```bash
# Non-existent category
curl "http://localhost:8080/api/search?category=NonExistentCategory"
# Expected: {"total":0,"courses":[],"pagination":{...}}

# Impossible age range
curl "http://localhost:8080/api/search?minAge=20&maxAge=25"
# Expected: {"total":0,"courses":[],"pagination":{...}}

# Very high price filter
curl "http://localhost:8080/api/search?minPrice=500"
# Expected: {"total":0,"courses":[],"pagination":{...}}
```

#### Invalid Parameters
```bash
# Invalid sort parameter (falls back to default)
curl "http://localhost:8080/api/search?sort=invalidSort"
# Expected: Results sorted by upcoming date (default behavior)

# Invalid page number (returns empty but valid response)
curl "http://localhost:8080/api/search?page=999"
# Expected: {"total":51,"courses":[],"pagination":{...}}
```

### 12. Response Format Validation

Every `/api/search` response follows this exact structure:

```json
{
  "total": 51,
  "courses": [
    {
      "id": "1",
      "title": "Math Adventures for Little Explorers",
      "category": "Math", 
      "price": 85.0,
      "nextSessionDate": "2025-06-10T15:00:00Z",
      "description": "Fun and interactive math activities...",
      "type": "COURSE",
      "gradeRange": "K-2nd",
      "minAge": 5,
      "maxAge": 7
    }
  ],
  "pagination": {
    "currentPage": 0,
    "pageSize": 10,
    "totalPages": 6,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

#### Required Response Fields:
- ✅ `total`: Total number of matching courses
- ✅ `courses`: Array of course objects with all required fields
- ✅ `pagination`: Complete pagination metadata

### 13. Autocomplete Testing

#### Autocomplete Suggestions
```bash
# Get suggestions for "art"
curl "http://localhost:8080/api/courses/autocomplete?query=art"
# Expected: ["Young Artists Club","Origami Art Workshop","Martial Arts Introduction","Knitting and Fiber Arts"]

# Get suggestions for "math"  
curl "http://localhost:8080/api/courses/autocomplete?query=math"
# Expected: ["Math Adventures for Little Explorers","Advanced Mathematics Challenge","Math Olympiad Preparation"]

# Get suggestions for "science"
curl "http://localhost:8080/api/courses/autocomplete?query=science"
# Expected: Multiple science-related course titles
```

#### Expected Autocomplete Behavior:
- ✅ Returns array of course titles (not full course objects)
- ✅ Matches based on course title
- ✅ Limited to relevant suggestions (typically 3-5 results)
- ✅ Fast response time for real-time suggestions

---
