<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# Course Search Application - Spring Boot + Elasticsearch

This is a Spring Boot application that demonstrates Elasticsearch integration for course search functionality.

## Project Structure
- **Main Package**: `com.example.coursesearch`
- **Dependencies**: Spring Web, Spring Data Elasticsearch, Lombok
- **Java Version**: 17
- **Build Tool**: Maven

## Key Requirements
1. Index sample "course" documents into Elasticsearch
2. Expose REST endpoints for course search with:
   - Multiple filters
   - Pagination support
   - Sorting capabilities
3. (Bonus) Implement autocomplete suggestions and fuzzy matching

## Development Guidelines
- Use Spring Data Elasticsearch annotations for entity mapping
- Implement proper DTOs for API requests/responses
- Follow REST API best practices
- Use Lombok annotations to reduce boilerplate code
- Implement comprehensive error handling
- Add appropriate logging

## Elasticsearch Configuration
- Local instance runs on `localhost:9200`
- Docker Compose provides Elasticsearch and Kibana
- Security is disabled for development

## Testing
- Use Testcontainers for integration tests
- Mock external dependencies appropriately
