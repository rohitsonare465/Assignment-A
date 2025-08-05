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

## Sample Data Initialization

The application automatically bulk-indexes sample course data from `src/main/resources/sample-courses.json` during startup.

### Automatic Data Loading

When the application starts, the `DataInitializer` component:

1. **Reads** `sample-courses.json` containing 50 sample courses
2. **Bulk-indexes** all courses into Elasticsearch's `courses` index
3. **Logs** the number of courses loaded successfully

**Startup Log Example**:
```
2025-08-05T17:13:37.016+05:30  INFO 66246 --- [main] c.e.c.component.DataInitializer : Initializing sample course data...
2025-08-05T17:13:37.043+05:30  INFO 66246 --- [main] c.e.coursesearch.service.CourseService : Saving 50 courses
2025-08-05T17:13:37.199+05:30  INFO 66246 --- [main] c.e.c.component.DataInitializer : Successfully loaded 50 sample courses
```

### Manual Data Loading

You can also trigger sample data loading manually via API:

```bash
# Load sample data manually
curl -X POST "http://localhost:8080/api/courses/load-sample-data"

# Response
{
  "message": "Sample data loaded successfully",
  "status": "success"
}
```

### Verify Data Ingestion

1. **Check total number of courses**:
   ```bash
   curl "http://localhost:8080/api/courses/search?size=1" | jq '.totalElements'
   ```

2. **List all courses with pagination**:
   ```bash
   curl "http://localhost:8080/api/courses/search?page=0&size=10"
   ```

3. **Search specific categories**:
   ```bash
   curl "http://localhost:8080/api/courses/search?category=Science"
   ```

4. **Check Elasticsearch directly**:
   ```bash
   curl "http://localhost:9200/courses/_count"
   ```

## API Endpoints

### Health & Management
- `GET /api/courses/health` - API health check
- `GET /actuator/health` - Application and Elasticsearch health status
- `POST /api/courses/load-sample-data` - Manually load sample data

### Course Management
- `POST /api/courses/bulk` - Bulk create/index courses
- `GET /api/courses/search` - Search courses with filters, pagination, sorting
- `POST /api/courses/search` - Search with JSON request body

### Search & Autocomplete (Bonus)
- `GET /api/courses/autocomplete?query={text}` - Get autocomplete suggestions

## Search Capabilities

### Basic Search Examples

1. **Get all courses with pagination**:
   ```bash
   curl "http://localhost:8080/api/courses/search?page=0&size=5"
   ```

2. **Search by text query**:
   ```bash
   curl "http://localhost:8080/api/courses/search?query=math"
   ```

3. **Filter by category**:
   ```bash
   curl "http://localhost:8080/api/courses/search?category=Science"
   ```

4. **Filter by type**:
   ```bash
   curl "http://localhost:8080/api/courses/search?type=COURSE"
   ```

5. **Age range filtering**:
   ```bash
   curl "http://localhost:8080/api/courses/search?minAge=8&maxAge=12"
   ```

6. **Price range filtering**:
   ```bash
   curl "http://localhost:8080/api/courses/search?minPrice=50&maxPrice=100"
   ```

7. **Combined filters**:
   ```bash
   curl "http://localhost:8080/api/courses/search?category=Math&type=COURSE&minPrice=50&maxPrice=150"
   ```

8. **Sorting**:
   ```bash
   # Sort by price ascending
   curl "http://localhost:8080/api/courses/search?sortBy=price&sortDirection=asc"
   
   # Sort by next session date descending
   curl "http://localhost:8080/api/courses/search?sortBy=nextSessionDate&sortDirection=desc"
   ```

### Advanced Search with JSON

```bash
curl -X POST "http://localhost:8080/api/courses/search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "programming",
    "category": "Technology",
    "minAge": 10,
    "maxAge": 15,
    "minPrice": 80,
    "maxPrice": 200,
    "page": 0,
    "size": 10,
    "sortBy": "price",
    "sortDirection": "asc"
  }'
```

### Autocomplete Suggestions

```bash
curl "http://localhost:8080/api/courses/autocomplete?query=art"
# Returns: ["Young Artists Club","Origami Art Workshop","Martial Arts Introduction","Knitting and Fiber Arts"]
```

### Available Filter Parameters

| Parameter | Type | Description | Example |
|-----------|------|-------------|---------|
| `query` | String | Search in title and description | `math`, `programming` |
| `category` | String | Course category | `Math`, `Science`, `Art` |
| `type` | String | Course type | `COURSE`, `CLUB`, `ONE_TIME` |
| `gradeRange` | String | Grade range | `K-2nd`, `3rd-8th` |
| `minAge` | Integer | Minimum age | `5`, `10` |
| `maxAge` | Integer | Maximum age | `12`, `18` |
| `minPrice` | Double | Minimum price | `50.0`, `100.0` |
| `maxPrice` | Double | Maximum price | `150.0`, `200.0` |
| `page` | Integer | Page number (0-based) | `0`, `1`, `2` |
| `size` | Integer | Page size | `5`, `10`, `20` |
| `sortBy` | String | Sort field | `price`, `nextSessionDate`, `title` |
| `sortDirection` | String | Sort direction | `asc`, `desc` |

### Sample Course Data

The application includes 50 sample courses with the following categories:
- **Math**: Basic and advanced mathematics courses
- **Science**: Physics, chemistry, biology, environmental science
- **Technology**: Programming, robotics, game design
- **Art**: Visual arts, crafts, creative projects
- **Music**: Performance, instruments, theory
- **Sports**: Various physical activities and team sports
- **Language Arts**: Writing, reading, literature
- **Life Skills**: Practical skills and personal development
- **Logic**: Chess, puzzles, critical thinking
- **Entertainment**: Shows, performances, fun activities

Each course includes:
- Title and description
- Category and type (COURSE/CLUB/ONE_TIME)
- Grade range and age requirements
- Pricing information
- Next session date

### Development

The application includes:
- Elasticsearch configuration for local development
- Automatic sample course data indexing via `DataInitializer`
- Comprehensive search capabilities with multiple filters
- Pagination and sorting support
- Autocomplete suggestions with fuzzy matching
- Error handling and logging
- REST API with both GET and POST endpoints

### Docker Services

- **Elasticsearch**: http://localhost:9200
- **Kibana**: http://localhost:5601

### Testing

Run tests with:
```bash
mvn test
```

Integration tests use Testcontainers to spin up Elasticsearch instances automatically.

## Implementation Status 

### Assignment A - Core Requirements
- [x] **Elasticsearch Integration**: Fully configured with Spring Data Elasticsearch
- [x] **Course Document Indexing**: CourseDocument entity with proper annotations
- [x] **Bulk Data Loading**: DataInitializer component loads 50 sample courses at startup
- [x] **REST API Search**: Multiple filter parameters (category, type, age, price, etc.)
- [x] **Pagination**: Page-based pagination with configurable size
- [x] **Sorting**: Multi-field sorting with ascending/descending options
- [x] **Error Handling**: Comprehensive error handling and validation

###  Assignment B - Bonus Features
- [x] **Autocomplete Suggestions**: Real-time suggestions based on course titles
- [x] **Fuzzy Matching**: Text search across title and description fields
- [x] **Advanced Filtering**: Combined filters with AND logic
- [x] **Multiple Search Methods**: Both GET and POST endpoints
- [x] **Data Management**: Manual data loading endpoint

### 📊 Data Verification

```bash
# Total courses indexed: 51 (50 from sample + 1 test)
curl "http://localhost:9200/courses/_count"

# Course distribution by type:
# - COURSE: 30 courses
# - CLUB: 10 courses  
# - ONE_TIME: 10 courses
# - Test Course: 1 additional

# Categories available:
# Math, Science, Technology, Art, Music, Sports, 
# Language Arts, Life Skills, Logic, Entertainment
```

###  Quick Start Verification

1. **Start services**: `docker compose up -d`
2. **Run application**: `./mvnw spring-boot:run`
3. **Verify data loading**: Check logs for "Successfully loaded 50 sample courses"
4. **Test search**: `curl "http://localhost:8080/api/courses/search?category=Science"`
5. **Test autocomplete**: `curl "http://localhost:8080/api/courses/autocomplete?query=math"`

---
