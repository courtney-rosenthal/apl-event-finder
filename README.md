# APL Event Finder

This application provides advanced search capability and notifications
for Austin Public Library events.

STATUS: in development

This application scrapes the APL event calendar
(https://library.austintexas.gov/events) into a searchable index. The 
frontend application provides  (will provide) advanced search and creating of 
notifications based on search criteria.

There are four microservices in this project:

* app-scraper -- Scrapes the event calendar into a searchable index
* app-search-api -- REST API for searching the index
* (TBD: Service to manage notifications)
* app-ui -- Front end application

STATUS: The _app-scraper_ and _app-search-api_ services have MVP 
implementations. Next step is the _app-ui_ MVP. See the TODO.md document for 
short-term development plans.

## Development and Build

The microservices are coded in Kotlin using Spring Boot. The Gradle build 
system is used.

At this time the only additional service required is Elasticserach. A 
_docker-compose.yml_ file starts the services needed for development and test.

To build:

    docker-compose up [--detach]    # bring up services
    ./gradlew clean build -x check  # perform build (skip tests)
    ./gradlew check                 # perform tests

## Deployment

This package is not ready for deployment.

At this time, execution is being done through the IDE. Standalone execution 
(initially Gradle _bootrun_ tasks, eventually Docker containers) are TBD.

## Author

Courtney Rosenthal <cr@crosenthal.com>
