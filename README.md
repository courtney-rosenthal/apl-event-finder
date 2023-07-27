# APL Library Event Calendar Search & Notification

This application provides advanced search capability for
the Austin Public Library events calendar 
<https://library.austintexas.gov/events>.

There are four microservices in this project:

* app-scraper -- Scrapes the event calendar into a searchable index
* app-search-api -- REST API for searching the index
* app-ui -- Front end application
* (TBD: Service to manage notifications)

## Dev Environment

To build:

    docker-compose up
    ./gradlew build

TODO: add directions to run the services

## Author

Courtney Rosenthal <cr@crosentha.com>

