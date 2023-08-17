package jwt.utils.external;

import jwt.utils.wrappers.InvalidTokenException;
import jwt.utils.wrappers.JwtEngine;
import jwt.utils.wrappers.TokenGenerationException;

import java.time.LocalDateTime;

public class Jjwt implements JwtEngine {
    @Override
    public String generateToken(String keyId, String issuer, String subject, String audience, int expirationSecondsFromNow, String pemKey) throws TokenGenerationException {
        return null;
    }

    @Override
    public LocalDateTime getExpirationDate(String token) {
        return null;
    }

    @Override
    public void validateToken(String jwt, String encodedKey) throws InvalidTokenException, IllegalArgumentException {

    }
}
