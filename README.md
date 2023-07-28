# APL Event Finder

This application provides advanced search capability and notifications
for Austin Public Library events.

STATUS: in development

It scrapes the APL event calendar (https://library.austintexas.gov/events) into
a searchable index. The front end application provides (will provide) advanced 
search and creating of notifications based on search criteria.

There are four microservices in this project:

* app-scraper -- Scrapes the event calendar into a searchable index
* app-search-api -- REST API for searching the index
* app-ui -- Front end application
* (TBD: Service to manage notifications)

STATUS: The _app-scraper_ and _app-search-api_ services have MVP 
implementations. The _app-ui_ MVP is the next step.

## Development and Build

The microservices are coded in Kotlin using Spring Boot. The Gradle build 
system is used.

At this time the only additional service required is Elasticserach. A 
_docker-compose.yml_ file starts the services needed for development and test.

To build:

    docker-compose up [--detach]    # bring up services
    ./gradlew clean build -x check  # perform build (skip tests)
    ./gradlew check                 # perform tests

At this time, execution is being done through the IDE. Standalone execution 
(initially Gradle _bootrun_ tasks, eventually Docker containers) are TBD.

See the TODO.md file for pending tasks.

## Author

Courtney Rosenthal <cr@crosenthal.com>
