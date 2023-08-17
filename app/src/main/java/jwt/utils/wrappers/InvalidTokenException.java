package jwt.utils.wrappers;

public class InvalidTokenException extends Exception {

    /** Occurs when an invalid JWT is parsed */
    public InvalidTokenException(Throwable e) {
        super(e);
    }
}
