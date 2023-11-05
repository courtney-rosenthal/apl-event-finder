# To Do

## Current Tasks
* implement search by tags
* app-scraper - cleanup EventScraper
* implement missing tests
* figure out why logging (e.g. Elasticsearch loggers) isn't working

## Backlog Tasks
* lib-elasticsearch - filter out past events and deleted events
* app-scraper - rework how ScrapeIssues are recorded and reported
* app-scraper - add config to force https
* app-search-api - add pagination (backend)
* app-ui - add pagination (frontend)

## Tech Debt
* app-ui - move API base URL to config
* app-scraper - move localZoneId to config

## Future Tasks
* move form content out of app-ui into a service
* app-scraper - add Redis and record scrapes (to track adds/deletes)
* app-scraper - add mode to just check for adds/deletes
* app-scraper - add periodic scheduling for scrapes
