package jwt.utils.wrappers;

public class TokenGenerationException extends Exception {


    /** Thrown when the {@link JwtEngine} cannot create a token. */
    public TokenGenerationException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }
}
