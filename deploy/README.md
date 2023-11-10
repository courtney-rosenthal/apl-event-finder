# Deployment Instructions

There are two parts to the deployment: font-end and backend.

## Front-End Deployment

The front end currently runs in Github Pages. It is automatically deployed 
through Github Actions. This is controlled by the _.github/workflows/build.yml_
file.

## Backend Deployment

The back end is provided through Docker images, published at _ghcr.io_.

To deploy the backend:

* Copy the _deploy_ directory onto a server with Docker installed.
* Copy the _env.example_ file to _.env_.
* Edit and configure the _.env_ file.
* Run `docker compose up -d` to start the backend stack.
* Startup can be monitored with: `docker compose logs -f`

The Docker Compose stack includes _Traefik_ reverse proxy configured to 
acquire a Let's Encrypt _ssl_ certificate. It may take a little while to 
initialize the first time.

You can verify the backend is running at:
https://HOSTNAME/swagger-ui/index.html

## Content Scrape

**TODO**