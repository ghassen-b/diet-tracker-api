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

### docker-compose deployment

To deploy these components, you simply need to go to the `dev-tools` directory and run:

```
docker-compose up -d
```

Then to remove them:

```
docker-compose down
```

## Starting the app

Once the required third-party tools are deployed, simple run the following line from the current directory:

```
mvn spring-boot:run
```

# Testing locally

## Running unit tests

```
mvn test
```

## Running integration tests

```
mvn verify
```
