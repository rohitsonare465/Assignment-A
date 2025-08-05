# Development Notes

## TODO
- [ ] Add pagination to autocomplete suggestions
- [ ] Implement caching for frequent searches
- [ ] Add metrics/monitoring for search performance
- [ ] Consider adding search analytics

## Known Issues
- Fuzzy search might be slow for very large datasets
- Need to tune Elasticsearch settings for production

## Testing Notes
- Integration tests require Docker
- Sample data has 51 courses
- Fuzzy search works well for 1-2 character typos

## Deployment
- Make sure Elasticsearch is running before starting app
- Check application.properties for environment-specific settings
