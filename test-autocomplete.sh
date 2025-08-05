#!/bin/bash

echo "=== Course Search Autocomplete API Test ==="
echo ""

BASE_URL="http://localhost:8080"

# Function to test endpoint and show results
test_suggest() {
    local query="$1"
    echo "Testing suggest endpoint with query: '$query'"
    echo "curl \"$BASE_URL/api/search/suggest?q=$query\""
    curl -s "$BASE_URL/api/search/suggest?q=$query" | python3 -m json.tool
    echo ""
    echo "---"
    echo ""
}

# Function to test search endpoint to verify autocomplete suggestions work with search
test_search() {
    local query="$1"
    echo "Testing search endpoint with query: '$query'"
    echo "curl \"$BASE_URL/api/search?q=$query\""
    curl -s "$BASE_URL/api/search?q=$query" | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    print(f'Found {data[\"total\"]} courses:')
    for course in data['courses'][:3]:  # Show first 3 results
        print(f'  - {course[\"title\"]} ({course[\"category\"]})')
    if data['total'] > 3:
        print(f'  ... and {data[\"total\"] - 3} more')
except Exception as e:
    print(f'Error: {e}')
"
    echo ""
    echo "---"
    echo ""
}

echo "1. Testing Autocomplete Suggestions"
echo "================================="

# Test various autocomplete queries
test_suggest "mat"
test_suggest "sci"
test_suggest "art"
test_suggest "comp"
test_suggest "music"
test_suggest "cook"

echo "2. Testing Edge Cases"
echo "==================="

# Test edge cases
test_suggest ""
test_suggest "xyz"
test_suggest "z"

echo "3. Verifying Suggestions Work with Search"
echo "========================================"

# Test that suggested titles actually work with search
echo "Testing that suggested titles work in search..."
echo ""

# Get suggestions for "mat" and test searching for the first suggestion
SUGGESTION=$(curl -s "$BASE_URL/api/search/suggest?q=mat" | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    if data['suggestions']:
        print(data['suggestions'][0])
except:
    pass
")

if [ ! -z "$SUGGESTION" ]; then
    echo "First suggestion for 'mat': $SUGGESTION"
    test_search "$SUGGESTION"
fi

echo "4. Testing Help Documentation"
echo "============================"

echo "Testing help endpoint includes autocomplete documentation:"
echo "curl \"$BASE_URL/api/search/help\""
curl -s "$BASE_URL/api/search/help" | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    if 'suggestEndpoint' in data:
        suggest = data['suggestEndpoint']
        print(f'✓ Suggest endpoint documented: {suggest[\"endpoint\"]}')
        print(f'✓ Description: {suggest[\"description\"]}')
        print(f'✓ Parameters: {suggest[\"parameters\"]}')
    if 'examples' in data and 'Autocomplete suggestions' in data['examples']:
        print(f'✓ Example: {data[\"examples\"][\"Autocomplete suggestions\"]}')
    else:
        print('✗ Autocomplete example not found in help')
except Exception as e:
    print(f'Error parsing help: {e}')
"

echo ""
echo "=== Test Complete ==="
echo ""
echo "Expected behavior:"
echo "- Suggestions should return relevant course titles starting with the query"
echo "- Empty or non-matching queries should return empty suggestions"
echo "- Each suggestion should be a valid course title that works with search"
echo "- Help endpoint should document the suggest API"
