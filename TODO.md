# To Do

## Current Tasks
* finish implementing location (verify search works)
* app-scraper - cleanup EventScraper
* figure out why logging (e.g. Elasticsearch loggers) isn't working

## Backlog Tasks
* app-ui - implement tags
* lib-elasticsearch - filter out past events and deleted events
* app-scraper - rework how ScrapeIssues are recorded and reported
* app-ui - move API base URL to config
* app-scraper - add config to force https
* app-search-api - add pagination (backend)
* app-ui - add pagination (frontend)
* app-scraper - move localZoneId to config

## Future Tasks
* move form content out of app-ui into a service
* app-scraper - add Redis and record scrapes (to track adds/deletes)
* app-scraper - add mode to just check for adds/deletes
* app-scraper - add periodic scheduling for scrapes
