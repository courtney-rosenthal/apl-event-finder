# To Do

## Current Tech Debt
* implement missing tests
* cleanup EventScraper class
* figure out why Elasticsearch loggers aren't working

## New Capabilities
* Implement results pagination in the UI
* Put deployment docker-compose into git repo
* Add traefik to docker-compose and implement SSL
* Implement periodic update of index (and filter past events from results)

## Backlog Tech Debt
* implement health check in docker-compose
* add config to force https (to avoid all the redirections when pulling events)
* move localZoneId to config
* test ssl and auth for ElasticSearch
* add test coverage report to build

## Future Tasks
* move form content out of app-ui into a service
* app-scraper - add Redis and record scrapes (to track adds/deletes)

## Issues Observed

Some events don't have a summary (although they do have description text), 
so the listing summary is blank.

    Example:
    http://library.austintexas.gov/event/books-and-babies/books-and-babies-7736788