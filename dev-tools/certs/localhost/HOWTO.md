# Certificate to use when Keycloak is running the "localhost" hostname

To generate the files contained in this directory:

```
openssl genrsa -out keycloak.key 2048
openssl req -new -key keycloak.key -out keycloak.csr -subj "/CN=localhost"
openssl x509 -req -in keycloak.csr -signkey keycloak.key -out keycloak.crt -days 365
keytool -importcert -file keycloak.crt -keystore truststore.jks -storepass truststorepassword -alias keycloak-cert -noprompt
```

Not that the Common Name is *localhost*. These are to be used when running the app in a non-containerized way.
