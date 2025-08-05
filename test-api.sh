#!/bin/bash
# test-api.sh - Comprehensive API testing script for Course Search Application

BASE_URL="http://localhost:8080"
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Testing Course Search API at $BASE_URL${NC}"
echo "=============================================="

# Function to test API endpoint and validate response
test_endpoint() {
    local test_name="$1"
    local url="$2"
    local expected_condition="$3"
    
    echo -e "\n${YELLOW}$test_name${NC}"
    echo "URL: $url"
    
    response=$(curl -s "$url")
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Request successful${NC}"
        echo "Response: $response" | jq '.' 2>/dev/null || echo "$response"
        
        # Validate expected condition if provided
        if [ ! -z "$expected_condition" ]; then
            result=$(echo "$response" | jq -r "$expected_condition" 2>/dev/null)
            echo "Validation: $result"
        fi
    else
        echo -e "${RED}✗ Request failed${NC}"
    fi
    echo "----------------------------------------------"
}

# Test 1: Health check
test_endpoint "1. Health Check" \
    "$BASE_URL/actuator/health" \
    '.status'

# Test 2: API Documentation
test_endpoint "2. API Documentation" \
    "$BASE_URL/api/search/help" \
    '.endpoint'

# Test 3: Get all courses (basic search)
test_endpoint "3. Get All Courses" \
    "$BASE_URL/api/search" \
    '.total'

# Test 4: Search by keyword
test_endpoint "4. Search by Keyword 'math'" \
    "$BASE_URL/api/search?q=math" \
    '.total'

# Test 5: Filter by category
test_endpoint "5. Filter by Science Category" \
    "$BASE_URL/api/search?category=Science" \
    '.total'

# Test 6: Filter by type
test_endpoint "6. Filter by ONE_TIME Type" \
    "$BASE_URL/api/search?type=ONE_TIME" \
    '.total'

# Test 7: Age range filtering
test_endpoint "7. Age Range Filter (10-15)" \
    "$BASE_URL/api/search?minAge=10&maxAge=15" \
    '.total'

# Test 8: Price range filtering
test_endpoint "8. Price Range Filter (\$50-\$100)" \
    "$BASE_URL/api/search?minPrice=50&maxPrice=100" \
    '.total'

# Test 9: Sort by price ascending
test_endpoint "9. Sort by Price (Ascending)" \
    "$BASE_URL/api/search?sort=priceAsc&size=3" \
    '.courses[0].price'

# Test 10: Sort by price descending
test_endpoint "10. Sort by Price (Descending)" \
    "$BASE_URL/api/search?sort=priceDesc&size=3" \
    '.courses[0].price'

# Test 11: Sort by upcoming date
test_endpoint "11. Sort by Upcoming Date" \
    "$BASE_URL/api/search?sort=upcoming&size=3" \
    '.courses[0].nextSessionDate'

# Test 12: Pagination - First page
test_endpoint "12. Pagination - First Page" \
    "$BASE_URL/api/search?page=0&size=5" \
    '.pagination.currentPage'

# Test 13: Pagination - Second page
test_endpoint "13. Pagination - Second Page" \
    "$BASE_URL/api/search?page=1&size=5" \
    '.pagination.currentPage'

# Test 14: Combined filters
test_endpoint "14. Combined Filters (Technology + Age Range + Price Sort)" \
    "$BASE_URL/api/search?category=Technology&minAge=10&maxAge=18&sort=priceAsc" \
    '.total'

# Test 15: Complex query with all parameters
test_endpoint "15. Complex Query (All Parameters)" \
    "$BASE_URL/api/search?q=programming&category=Technology&type=COURSE&minAge=8&maxAge=16&minPrice=80&maxPrice=150&sort=priceAsc&page=0&size=10" \
    '.total'

# Test 16: Empty result set
test_endpoint "16. Empty Result Set" \
    "$BASE_URL/api/search?category=NonExistentCategory" \
    '.total'

# Test 17: Autocomplete suggestions
test_endpoint "17. Autocomplete Suggestions for 'art'" \
    "$BASE_URL/api/courses/autocomplete?query=art" \
    'length'

# Test 18: Autocomplete with different query
test_endpoint "18. Autocomplete Suggestions for 'math'" \
    "$BASE_URL/api/courses/autocomplete?query=math" \
    'length'

# Test 19: Legacy endpoint compatibility
test_endpoint "19. Legacy Endpoint (/api/courses/search)" \
    "$BASE_URL/api/courses/search?category=Math" \
    '.totalElements'

# Summary
echo -e "\n${GREEN}=============================================="
echo "API Testing Complete!"
echo -e "=============================================${NC}"

# Performance test
echo -e "\n${YELLOW}Performance Test${NC}"
echo "Measuring response time for large result set..."
time curl -s "$BASE_URL/api/search?size=20" > /dev/null
echo "Response time measurement complete."

echo -e "\n${GREEN}All tests finished!${NC}"
echo "Check the output above for any failures or unexpected results."
echo "Expected behaviors:"
echo "- Health check should return 'UP'"
echo "- Search results should have positive totals"
echo "- Pagination should show correct currentPage"
echo "- Sorting should order results appropriately"
echo "- Autocomplete should return relevant suggestions"
