For dev purposes, the .p12 file was generated using the following command line:

```
keytool -genkeypair -alias diet-tracker-app -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore diet-tracker-app.p12 -validity 3650
```

It's secret is `secret` (dah, it's for development & testing purposes only!)

When using this certificate, you'll need to add the `-k` option to curl, since it's a self-signed certificate.