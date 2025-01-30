# MySQL

To connect to the DB (e.g. for debugging & checking):

```
docker exec -it mysql mysql -u myuser -p
```

... then type the password

# Keycloak

## Exporting realm with users

https://simonscholz.dev/tutorials/keycloak-realm-export-import

But it does not work with keycloak >=26: https://github.com/keycloak/keycloak/issues/33800

Hack:

* Change the docker compose for Keycloak to use H2 db (comment the env vars that provide the DB info)
* In the keycloak container, run:
```
cp -rp /opt/keycloak/data/h2 /tmp
/opt/keycloak/bin/kc.sh export --dir /tmp --users realm_file --realm diet-app-realm --db dev-file --db-url 'jdbc:h2:file:/tmp/h2/keycloakdb;NON_KEYWORDS=VALUE'
```
* From outside the container, run:
```
docker cp keycloak:/tmp/diet-app-realm-realm.json /path-to-repo/dev-tools/keycloak/dev-keycloak-data.json
```