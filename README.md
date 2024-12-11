# Diet Tracker API

This is a REST API that will help you keep track of your diet content on a daily basis. 

It can help you answer the following questions: "How frequently am I eating meat? Is my diet really *mostly vegetarian?, ..."

## Behind the code

This project started as a side-project for me as I was learning SpringBoot.

As a result, I tried to stick to the following principles throughout the project development:

* The code should always be clean: no dead code, no useless items
* The code should be clearly documented: inline documentation, READMEs, API documentation (to be implemented)
* Testing:
    - Unit tests should be *really* fast to run: avoid starting the whole application in order to test one single service #LongLiveTheMocks
    - Unit tests coverage should be close to 100%
    - Integration tests should avoid mocks: when you reach the IT step, you should run the application in real life-like scenarios
    - Integration tests coverage should *also* be close to 100%
* Application deployment should be as easy as possible

# Deploying the application

## Requirements

The app needs these components to be deployed:
* An external MySQL DB (it will be accessed locally on port 3306).

* An external Keycloak instance (it will be accessed locally on port 8080). A diet-app-realm realm is used by the app.

You can deploy the app & its dependencies in two (main) ways:

## Containerized application

This is best suited when you simply want to deploy the app and interact with it through the API's endpoints.

You simply need to run:

```
docker-compose up --wait
```

This will
- Deploy a MySQL instance
- Deploy a Keycloak instance and prepopulate it with a default user
- Package the application into a Docker image (if an image tagged `diet-tracker-api:latest` is not present in your local Docker registry)
- Run the app and configure it to interact with the previously deployed MySQL & Keycloak containers
- Fill the app's database with some default values

If you deployed the app once, then made some changes to the source code, and want to re-package it before redeploying it, don't forget to run 

```
docker-compose up --wait --build
```
## Easier to debug deployment

If you want to be able to run the application locally, or run it in "debug" mode through your IDE, the containerized deployment is not the best (even though it's possible).

You should first deploy the third-party components the app depends on:

```
docker-compose up -f docker-compose-only-third-party.yml --wait
```

This will
- Deploy a MySQL instance
- Deploy a Keycloak instance and prepopulate it with a default user

You can then start the app manually, through your IDE, or by running

```
mvn spring-boot:run
```

## Docker image generation only

If you only want to package the application as a Docker image, run

```
docker build -t diet-tracker-api:latest .
```

## Cleanup

To cleanup the deployed containers, run

```
docker-compose down
```

# Running the tests

## Running unit tests

```
mvn test
```

## Running integration tests

```
mvn verify
```

# OpenAPI Documentation

Once you deploy the application, you can access the openAPI (Swagger) documentation through:

* `https://localhost:8090/api-docs`: provides a JSON-formatted specification

* `https://localhost:8090/swagger-ui.html`: provides a user-friendly UI to navigate the documentation
