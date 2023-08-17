package jwt.utils.external;

import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jwt.utils.wrappers.InvalidTokenException;
import jwt.utils.wrappers.JwtEngine;
import jwt.utils.wrappers.TokenGenerationException;
import io.jsonwebtoken.Claims;
import javax.annotation.Nonnull;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public class Jjwt implements JwtEngine {

    private static final Jjwt INSTANCE = new Jjwt();

    public Jjwt() {}
    public static Jjwt getInstance() {
        return INSTANCE;
    }
    @Override
    public String generateToken(
            String keyId,
            String issuer,
            String subject,
            String audience,
            int expirationSecondsFromNow,
            String pemKey)
            throws TokenGenerationException {

        Key privateKey;
        try {
            privateKey = readKey(pemKey);
        } catch (NoSuchAlgorithmException e) {
            throw new TokenGenerationException("The private key algorithm isn't supported", e);
        } catch (Exception e) {
            throw new TokenGenerationException("The private key wasn't formatted correctly", e);
        }

        JwtBuilder jwsObj;
        try {
            jwsObj =
                    Jwts.builder()
                            .setHeaderParam("kid", keyId)
                            .setHeaderParam("typ", "JWT")
                            .setIssuer(issuer)
                            .setSubject(subject)
                            .setAudience(audience)
                            .setExpiration(
                                    new Date(
                                            System.currentTimeMillis()
                                                    + (expirationSecondsFromNow * 1000L)))
                            .setId(UUID.randomUUID().toString())
                            .signWith(privateKey);

            return jwsObj.compact();
        } catch (JwtException exception) {
            throw new TokenGenerationException(
                    "Jjwt was unable to create or sign the JWT", exception);
        }
    }

    @Override
    public LocalDateTime getExpirationDate(String jwt) {

        var tokenOnly = jwt.substring(0, jwt.lastIndexOf('.') + 1);

        Claims claims;
        try {
            claims = Jwts.parserBuilder().build().parseClaimsJwt(tokenOnly).getBody();
        } catch (ClaimJwtException e) {
            claims = e.getClaims();
        }

        Date expirationDate = claims.getExpiration();
        return LocalDateTime.ofInstant(expirationDate.toInstant(), ZoneId.systemDefault());
    }

    @Override
    public void validateToken(String jwt, String encodedKey)
            throws InvalidTokenException, IllegalArgumentException {

        try {
            var key = readKey(encodedKey);
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);

        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("The key algorithm isn't supported", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("The key wasn't formatted correctly", e);
        }
    }

    protected Key readKey(String encodedKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalArgumentException {
        return isPrivateKey(encodedKey) ? readPrivateKey(encodedKey) : readPublicKey(encodedKey);
    }

    protected boolean isPrivateKey(String key) {

        try {
            readPrivateKey(key);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected PrivateKey readPrivateKey(@Nonnull String pemKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalArgumentException {

        byte[] encode = parseBase64(pemKey);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encode);
        return keyFactory.generatePrivate(keySpec);
    }

    protected PublicKey readPublicKey(@Nonnull String pemKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalArgumentException {

        byte[] encode = parseBase64(pemKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encode);
        return keyFactory.generatePublic(keySpec);
    }

    protected byte[] parseBase64(String keyString) throws IllegalArgumentException {
        return Base64.getDecoder().decode(stripPemKeyHeaderAndFooter(keyString));
    }

    private String stripPemKeyHeaderAndFooter(String pemKey) {
        return pemKey.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "");
    }
}
