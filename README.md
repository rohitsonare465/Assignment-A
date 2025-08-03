# Course Search Application

A Spring Boot application that demonstrates Elasticsearch integration for course search functionality.

## Features

- **Assignment A**: Course indexing and search with filters, pagination, and sorting
- **Assignment B (Bonus)**: Autocomplete suggestions and fuzzy matching

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data Elasticsearch**
- **Lombok**
- **Maven**
- **Docker & Docker Compose**
- **Elasticsearch 8.11.0**
- **Kibana 8.11.0**

## Project Structure

```
src/
├── main/
│   ├── java/com/example/coursesearch/
│   │   ├── CourseSearchApplication.java
│   │   ├── model/          # Course entity and DTOs
│   │   ├── repository/     # Elasticsearch repositories
│   │   ├── service/        # Business logic
│   │   ├── controller/     # REST controllers
│   │   └── config/         # Configuration classes
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/example/coursesearch/
        └── CourseSearchApplicationTests.java
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose

### Setup

1. **Start Elasticsearch and Kibana**:
   ```bash
   docker-compose up -d
   ```

2. **Verify Elasticsearch is running**:
   ```bash
   curl http://localhost:9200
   ```

3. **Build the application**:
   ```bash
   mvn clean compile
   ```

4. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

### API Endpoints

- **Course Management**:
  - `POST /api/courses` - Create/Index a course
  - `GET /api/courses/{id}` - Get course by ID
  - `PUT /api/courses/{id}` - Update course
  - `DELETE /api/courses/{id}` - Delete course

- **Search**:
  - `GET /api/courses/search` - Search courses with filters, pagination, sorting
  - `GET /api/courses/suggest` - Autocomplete suggestions (Bonus)

### Development

The application includes:
- Elasticsearch configuration for local development
- Sample course data indexing
- Comprehensive search capabilities
- Error handling and logging
- Integration tests with Testcontainers

### Docker Services

- **Elasticsearch**: http://localhost:9200
- **Kibana**: http://localhost:5601

### Testing

Run tests with:
```bash
mvn test
```

Integration tests use Testcontainers to spin up Elasticsearch instances automatically.
