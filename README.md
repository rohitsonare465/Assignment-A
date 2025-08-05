# Course Search Application

A Spring Boot application demonstrating Elasticsearch integration for course search functionality.

## Features

- **Core**: Course indexing and search with filters, pagination, and sorting
- **Bonus**: Autocomplete suggestions and fuzzy matching

## Technology Stack

- **Java 17** | **Spring Boot 3.2.0** | **Spring Data Elasticsearch** | **Maven** | **Docker**

## 🚀 Quick Start

1. **Start services**: `docker compose up -d`
2. **Run application**: `./mvnw spring-boot:run`
3. **Verify**: Check logs for "Successfully loaded 50 sample courses"
4. **Test**: `curl "http://localhost:8080/api/search?category=Science"`

## 📚 API Documentation

### Main Search Endpoint: `/api/search`

**Parameters:**
- `q` - Search keyword (title/description)
- `category` - Course category filter
- `type` - Course type (`ONE_TIME`, `COURSE`, `CLUB`)
- `minAge`, `maxAge` - Age range filters
- `minPrice`, `maxPrice` - Price range filters
- `startDate` - Courses starting after date (ISO-8601)
- `sort` - Sort order (`upcoming`, `priceAsc`, `priceDesc`)
- `page`, `size` - Pagination (default: page=0, size=10)

**Example Requests:**
```bash
# Basic search
curl "http://localhost:8080/api/search?q=math"

# Filter by category and sort by price
curl "http://localhost:8080/api/search?category=Science&sort=priceAsc"

# Complex filter
curl "http://localhost:8080/api/search?category=Technology&minAge=10&maxAge=18&sort=priceAsc"

# API documentation
curl "http://localhost:8080/api/search/help"
```

### Autocomplete Endpoint: `/api/courses/autocomplete`

```bash
curl "http://localhost:8080/api/courses/autocomplete?query=art"
```

## 🧪 Testing

### Run Tests
```bash
# All tests (unit + integration)
./mvnw test

# Integration tests only (requires Docker)
./mvnw test -Dtest=CourseSearchIntegrationTest

# Automated API testing
chmod +x test-api.sh && ./test-api.sh
```

### Expected Data
- **Total courses**: 51
- **Categories**: Science (10), Technology (4), Math (4), Art (3), etc.
- **Types**: COURSE (31), CLUB (10), ONE_TIME (10)
- **Price range**: $15 - $180

## 🔧 Configuration

**Elasticsearch**: `localhost:9200` (via Docker Compose)
**Application**: `localhost:8080`
**Sample Data**: Auto-loaded from `src/main/resources/sample-courses.json`

## 📁 Project Structure

```
src/main/java/com/example/coursesearch/
├── controller/     # REST endpoints
├── service/        # Business logic
├── repository/     # Elasticsearch repos
├── model/          # Course entity
├── dto/            # Request/response objects
└── config/         # Elasticsearch config
```

## 🏃‍♂️ Development

```bash
# Build
./mvnw clean compile

# Run with live reload
./mvnw spring-boot:run

# Package
./mvnw package

# Run tests
./mvnw test
```
