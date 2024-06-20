# diet-tracker-api
REST API that helps you track your diet content.

# Deploying an external PostgreSQL DB 

You need a running MySQL/MariaDB. It will be accessed locally on port 3306.

For instance:

```
docker run --name test-api-db -e MYSQL_USER=myuser -e MYSQL_PASSWORD=mypassword -e MYSQL_DATABASE=api_diet_db -e MYSQL_ROOT_PASSWORD=root -d -p 3306:3306 mysql:8
```

Connecting to it (e.g. for debugging & checking):

```
docker exec -it test-api-db mysql -u myuser -p
```

... then type the password

# Playing with the API

You can load the provided Diet Tracker API.postman_collection.json in POsstman to play around with the API.
You will need to set two environment variables:
- IP
- PORT

