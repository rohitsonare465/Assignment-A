#!/bin/bash

echo "=== Course Search Fuzzy Search Test ==="
echo ""

BASE_URL="http://localhost:8080"

# Function to test fuzzy search
test_fuzzy() {
    local query="$1"
    local description="$2"
    echo "Testing: $description"
    echo "Query: '$query'"
    echo "curl \"$BASE_URL/api/search?q=$query\""
    curl -s "$BASE_URL/api/search?q=$query" | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    print(f'Found {data[\"total\"]} courses:')
    for course in data['courses']:
        print(f'  - {course[\"title\"]} ({course[\"category\"]})')
except Exception as e:
    print(f'Error: {e}')
"
    echo ""
    echo "---"
    echo ""
}

echo "1. Exact Matches (Baseline)"
echo "=========================="

test_fuzzy "dinosaur" "Exact search for 'dinosaur'"
test_fuzzy "mathematics" "Exact search for 'mathematics'"
test_fuzzy "science" "Exact search for 'science'"

echo "2. Fuzzy Matches with Single Character Typos"
echo "==========================================="

test_fuzzy "dinasaur" "Missing 'o' in dinosaur"
test_fuzzy "dinosour" "Changed 'a' to 'o' in dinosaur"
test_fuzzy "mathamatics" "Changed 'e' to 'a' in mathematics"
test_fuzzy "mathemetics" "Changed 'a' to 'e' in mathematics"
test_fuzzy "scince" "Missing 'e' in science"
test_fuzzy "sience" "Missing 'c' in science"

echo "3. Multiple Character Variations"
echo "==============================="

test_fuzzy "dinasour" "Two changes in dinosaur"
test_fuzzy "dinosuar" "Transposed 'ar' to 'ua' in dinosaur"
test_fuzzy "mathmatics" "Missing 'e' in mathematics"

echo "4. Edge Cases - Significant Typos"
echo "================================"

test_fuzzy "dinors" "Very short form of dinosaur"
test_fuzzy "maths" "Short form of mathematics"
test_fuzzy "sci" "Very short form of science"

echo "5. Testing with Other Course Titles"
echo "=================================="

test_fuzzy "origami" "Exact match for origami"
test_fuzzy "origami" "Typo in origami (missing 'i')"
test_fuzzy "cookng" "Typo in cooking (missing 'i')"

echo "=== Fuzzy Search Test Complete ==="
echo ""
echo "Expected behavior:"
echo "- Single character typos should be found (insertions, deletions, substitutions)"
echo "- Very short queries or heavily misspelled words may not match"
echo "- Exact matches should always work"
echo "- Fuzzy matching improves user experience for common typing errors"
