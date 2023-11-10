# To Do

## New Capabilities -- major things to do next
* pagination of search results in the UI
* periodic update of index (and filter past events from results)

## Tech Debt
* cleanup EventScraper class
* figure out why Elasticsearch loggers aren't working
* implement health check in docker-compose
* add config to force https (to avoid all the redirections when pulling events)
* move localZoneId to config
* test ssl and auth for ElasticSearch
* add test coverage report to build

## Future Tasks
* new UI
* email notifications based on saved search criteria
* move search criteria content out of UI app to backend

## Issues Observed

Some events don't have a summary (although they do have description text), 
so the listing summary is blank.

    Example:
    http://library.austintexas.gov/event/books-and-babies/books-and-babies-7736788
