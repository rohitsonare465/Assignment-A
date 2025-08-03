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

### Elasticsearch Configuration

#### Default Configuration
The application is pre-configured to connect to Elasticsearch running on `localhost:9200`. No additional configuration is required for local development.

#### Configuration Properties
All Elasticsearch settings are defined in `src/main/resources/application.properties`:

```properties
# Elasticsearch cluster connection URL
spring.elasticsearch.uris=http://localhost:9200

# Connection timeout for establishing connection to Elasticsearch
spring.elasticsearch.connection-timeout=10s

# Socket timeout for individual requests
spring.elasticsearch.socket-timeout=60s
```

#### Custom Configuration
To connect to a different Elasticsearch instance, modify these properties:

1. **Different Host/Port**:
   ```properties
   spring.elasticsearch.uris=http://your-elasticsearch-host:9200
   ```

2. **Multiple Nodes** (for clustered setup):
   ```properties
   spring.elasticsearch.uris=http://node1:9200,http://node2:9200,http://node3:9200
   ```

3. **With Authentication** (if security is enabled):
   ```properties
   spring.elasticsearch.username=elastic
   spring.elasticsearch.password=your-password
   ```

4. **HTTPS Connection**:
   ```properties
   spring.elasticsearch.uris=https://localhost:9200
   ```

#### Environment Variables
You can also override configuration using environment variables:
```bash
export SPRING_ELASTICSEARCH_URIS=http://localhost:9200
export SPRING_ELASTICSEARCH_USERNAME=elastic
export SPRING_ELASTICSEARCH_PASSWORD=changeme
```

### Setup

1. **Start Elasticsearch and Kibana**:
   ```bash
   docker compose up -d
   ```

2. **Verify Elasticsearch is running**:
   ```bash
   curl http://localhost:9200
   ```
   Expected response:
   ```json
   {
     "name" : "elasticsearch",
     "cluster_name" : "es-docker-cluster",
     "tagline" : "You Know, for Search"
   }
   ```

3. **Build the application**:
   ```bash
   ./mvnw clean compile
   ```

4. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Verify application health**:
   ```bash
   curl http://localhost:8080/actuator/health
   ```

### API Endpoints

- **Health Check**:
  - `GET /actuator/health` - Application and Elasticsearch health status

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
