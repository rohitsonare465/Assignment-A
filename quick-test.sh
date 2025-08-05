#!/bin/bash

# Quick dev testing script

echo "Testing course search API..."

# Basic health check
echo "Health check:"
curl -s http://localhost:8080/actuator/health

echo -e "\n\nTesting basic search:"
curl -s "http://localhost:8080/api/search?q=math" | jq '.total'

echo -e "\nTesting autocomplete:"
curl -s "http://localhost:8080/api/search/suggest?q=sci" | jq '.count'

echo -e "\nDone!"
