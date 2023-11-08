# To Do

## Current Tech Debt
* implement missing tests
* cleanup EventScraper class
* figure out why Elasticsearch loggers aren't working
* CalendarEventService.search() should return events in chronological order

## New Capabilities
* Put deployment docker-compose into dist subdir
* Add traefik to docker-compose and implement SSL
* Implement paging of results
* Implement periodic update of index
* Filter out past events

## Backlog Tech Debt
* add config to force https (to avoid all the redirections when pulling events)
* move localZoneId to config
* test ssl and auth for ElasticSearch
* add test coverage report to build

## Future Tasks
* move form content out of app-ui into a service
* app-scraper - add Redis and record scrapes (to track adds/deletes)
