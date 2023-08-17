package jwt.utils.wrappers;

import jwt.utils.wrappers.InvalidTokenException;
import jwt.utils.wrappers.TokenGenerationException;

import java.time.LocalDateTime;

/**
 * This interface provides a blueprint for all auth. related transactions. For example,
 * generateSenderToken() generates a token using a private key.
 */

public interface JwtEngine {

    String generateToken(
            String keyId,
            String issuer,
            String subject,
            String audience,
            int expirationSecondsFromNow,
            String pemKey)
            throws TokenGenerationException;

    LocalDateTime getExpirationDate(String token);

    void validateToken(String jwt, String encodedKey)
            throws InvalidTokenException, IllegalArgumentException;
}
