# Course Search Application

A Spring Boot application demonstrating Elasticsearch integration for course search functionality.

## Features

- **Core**: Course indexing and search with filters, pagination, and sorting
- **Bonus (Assignment B)**: ✅ Autocomplete suggestions and fuzzy search with typo tolerance

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
- `q` - Search keyword (title/description) with **fuzzy matching** for typos
- `category` - Course category filter
- `type` - Course type (`ONE_TIME`, `COURSE`, `CLUB`)
- `minAge`, `maxAge` - Age range filters
- `minPrice`, `maxPrice` - Price range filters
- `startDate` - Courses starting after date (ISO-8601)
- `sort` - Sort order (`upcoming`, `priceAsc`, `priceDesc`)
- `page`, `size` - Pagination (default: page=0, size=10)

**Fuzzy Search Features:**
- **Typo tolerance**: Handles single character errors (insertions, deletions, substitutions)
- **Smart matching**: Combines exact matches with fuzzy matching for best results
- **Elasticsearch-powered**: Fast, scalable fuzzy search with automatic scoring

**Example Requests:**
```bash
# Basic search
curl "http://localhost:8080/api/search?q=math"

# Filter by category and sort by price
curl "http://localhost:8080/api/search?category=Science&sort=priceAsc"

# Complex filter
curl "http://localhost:8080/api/search?category=Technology&minAge=10&maxAge=18&sort=priceAsc"

# Fuzzy search with typos - handles common typing errors
curl "http://localhost:8080/api/search?q=dinasaur"     # finds "Dinosaur Discovery Day"
curl "http://localhost:8080/api/search?q=mathamatics" # finds "Advanced Mathematics Challenge"
curl "http://localhost:8080/api/search?q=scince"      # finds science courses

# API documentation
curl "http://localhost:8080/api/search/help"
```

### Autocomplete Endpoint: `/api/search/suggest`

**Parameters:**
- `q` - Query string for suggestions (required)

**Response:**
- `suggestions` - Array of course title suggestions containing the query (up to 10)
- `query` - Original query string  
- `count` - Number of suggestions returned

**Features:**
- **Substring matching**: Finds course titles containing the query anywhere in the title
- **Case-insensitive**: Matches regardless of case
- **Fuzzy matching**: Provides flexible search for better user experience
- **Elasticsearch-powered**: Uses Elasticsearch for fast, scalable search with fallback

**Examples:**
```bash
# Get suggestions for titles containing "mat"
curl "http://localhost:8080/api/search/suggest?q=mat"
# Response: {"suggestions":["Math Adventures for Little Explorers","Advanced Mathematics Challenge","Math Olympiad Preparation"],"query":"mat","count":3}

# Get suggestions for titles containing "sci" 
curl "http://localhost:8080/api/search/suggest?q=sci"
# Response: {"suggestions":["Science Spectacular Workshop","Environmental Science Explorer","Science Fair Preparation Club","Veterinary Science Workshop"],"query":"sci","count":4}

# Get suggestions for titles containing "art"
curl "http://localhost:8080/api/search/suggest?q=art"
# Response: {"suggestions":["Young Artists Club","Origami Art Workshop","Martial Arts Introduction","Knitting and Fiber Arts"],"query":"art","count":4}
```

### Legacy Autocomplete Endpoint: `/api/courses/autocomplete`

### Legacy Autocomplete Endpoint: `/api/courses/autocomplete`

**Note:** This endpoint is maintained for backward compatibility. Use `/api/search/suggest` for the official autocomplete API.

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

# Test autocomplete functionality
chmod +x test-autocomplete.sh && ./test-autocomplete.sh

# Test fuzzy search with typos
chmod +x test-fuzzy-search.sh && ./test-fuzzy-search.sh
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

### 🔍 Fuzzy Search Capabilities

The search endpoint includes intelligent fuzzy matching to handle common typing errors and improve search experience:

**Supported Error Types:**
- **Character substitution**: `dinasaur` → `dinosaur`
- **Character deletion**: `scince` → `science` 
- **Character insertion**: `mathamatics` → `mathematics`
- **Character transposition**: `dinosuar` → `dinosaur`

**Fuzzy Search Examples:**
```bash
# Typo examples that work with fuzzy search
curl "http://localhost:8080/api/search?q=dinasaur"     # Missing 'o' → finds "Dinosaur Discovery Day"
curl "http://localhost:8080/api/search?q=dinosour"     # Wrong vowel → finds "Dinosaur Discovery Day"  
curl "http://localhost:8080/api/search?q=mathamatics"  # Wrong vowel → finds "Advanced Mathematics Challenge"
curl "http://localhost:8080/api/search?q=scince"       # Missing 'e' → finds all science courses
curl "http://localhost:8080/api/search?q=sience"       # Missing 'c' → finds all science courses
```

**How it works:**
1. **Exact matching** is tried first for performance
2. **Fuzzy matching** is applied for typo tolerance  
3. Results are automatically ranked by relevance
4. Single-character errors are typically handled well
5. Very short queries or heavily misspelled words may not match
