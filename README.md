# APL Event Finder

This application provides advanced search capability and notifications
for Austin Public Library events.

STATUS: in development (see the [TODO.md]() file for current issues)

This application scrapes the APL event calendar
(https://library.austintexas.gov/events) into a searchable index. The 
frontend application provides  (will provide) advanced search and creating of 
notifications based on search criteria.

There are four microservices in this project:

* app-scraper -- Scrapes the event calendar into a searchable index
* app-search-api -- REST API for searching the index
* (TBD: Service to manage notifications)
* app-ui -- Front end application

STATUS: The three listed services currently have MVP implementations.
See the TODO.md document for short-term development plans.

## Demo Instance

There is a demo instance running at:
https://courtney-rosenthal.github.io/apl-event-finder/

The demo instance has a Swagger UI for its API here:
http://apl-event-svc.crosenthal.com:8080/swagger-ui/index.html

### Insecure Content - IMPORTANT!!!

The backend service currently does not have _https_ security implemented, so 
you will need to adjust your browser to allow "Insecure content" for this site.

Without this, requests to the backend service will be blocked by the browser 
with a pop-up error.

To work around this with a Google Chrome-based browser:
  * Browse to the demo site (link above)
  * Click on the lock (or other icon) next to the URL
  * Select: Site settings
  * Scroll down to the "Insecure content" setting
  * Change it to: Allow

## Development

The backend microservices are coded in _Kotlin_ using _Spring Boot_.
_Elasticsearch_ is used for data storage and searching.
_Gradle_ is used for builds.

The frontend is implemented with _PrimeVue_,
which is the _Vue.js_ framework and the _PrimeUI_ widget library.
It has been built and tested with _Node_ version 20.5.0.

### Full Stack Development

For full stack development, all the services and resources are run locally. 
To setup your environment, perform the following steps.

To build the entire project, from the project base directory run:

    ./gradlew build -x check

To start Elasticsearch (needed for tests and execution), run:

    docker-compose -f dev/docker-compose.yml up [-d]

The "-d" above is optional. Include it to "detach" and run the services in 
the background. Omit it to keep the services running in the foreground.

Next, to run the test suite, do:

    ./gradlew check

To perform a trial run of the scraper app (this will go to
_library.austintexas.gov_ and retrieve 10 upcoming events, do:

    ./gradlew app-scraper:bootRun

To start the API service, run:

    ./gradlew app-search-api:bootRun

You can verify the service is running with:

    $ curl http://localhost:8080/api/ping
    pong

The Swagger interface to the API will be available here:
http://localhost:8080/swagger-ui/index.html

The front-end can be launched with:

    cd app-ui
    npm run dev

The front-end will be available at:
http://localhost:5173/


### Front-End Development

The setup to do front-end development is much simpler than the above.

To run the front-end UI locally, using the backend service running for the 
demo site, do:

    cd app-ui
    VITE_API_BASE_URL="http://apl-event-svc.crosenthal.com:8080/api" npm run dev

The front-end will be available at:
http://localhost:5173/

## Deployment

*TO DO*


## Author

Courtney Rosenthal <cr@crosenthal.com>

The source to this project is here:
https://github.com/courtney-rosenthal/apl-event-finder

The license for use is here:
https://github.com/courtney-rosenthal/apl-event-finder/blob/main/LICENSE
