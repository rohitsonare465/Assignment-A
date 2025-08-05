# Course Search Application

A Spring Boot application with Elasticsearch integration for searching educational courses.

## What it does

- Search courses by title, description, category, age range, price, etc.
- Autocomplete suggestions as you type
- Fuzzy search that handles typos (pretty cool!)
- REST API with filters, sorting, and pagination

## Tech Stack

**Java 17** | **Spring Boot 3.2** | **Elasticsearch** | **Maven** | **Docker**

## Getting Started

Make sure you have Docker and Java 17+ installed.

1. **Start Elasticsearch**: `docker compose up -d`
2. **Run the app**: `./mvnw spring-boot:run`
3. **Check it works**: `curl "http://localhost:8080/api/search?category=Science"`

The app loads 51 sample courses on startup. Check the logs to make sure everything loaded properly.

## API Reference

### Search: `/api/search`

Basic course search with various filters.

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

**Note:** This endpoint is maintained for backward compatibility. Use `/api/search/suggest` for the official autocomplete API.

```bash
curl "http://localhost:8080/api/courses/autocomplete?query=art"
```

## Testing

Run all tests:
```bash
./mvnw test
```

Quick API test:
```bash
./quick-test.sh
```

More comprehensive testing:
```bash
# Test different functionality
./test-api.sh
./test-autocomplete.sh  
./test-fuzzy-search.sh
```

### Expected Data
- **Total courses**: 51
- **Categories**: Science (10), Technology (4), Math (4), Art (3), etc.
- **Types**: COURSE (31), CLUB (10), ONE_TIME (10)
- **Price range**: $15 - $180

## Configuration

- **Elasticsearch**: localhost:9200 
- **Application**: localhost:8080
- **Sample data**: Loaded automatically from JSON file

For different environments, copy `application.properties` to `application-{env}.properties`.

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

## Development

```bash
# Build
./mvnw compile

# Run 
./mvnw spring-boot:run

# Package for deployment
./mvnw package
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
