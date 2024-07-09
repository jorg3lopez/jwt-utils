# jwt-utils
Utility app to decode and encode JWTs


## Creating a jwt
### This will generate a jwt customized for RS
``` ./ti generate-jwt  -p "path-to-private-key/private-key.pem"```

### This will create a jwt with the given fields
``` ./ti generate-jwt -i "the-issuer" -s "the-subject" -a "the-audience" -p "path-to-private-key/private-key.pem"```