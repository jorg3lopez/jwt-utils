package jwt.utils.wrappers

import spock.lang.Specification

class InvalidTokenExceptionTest extends Specification {
    def "constructor works"() {
        given:
        def cause = new IllegalArgumentException()

        when:
        def exception = new InvalidTokenException(cause)

        then:
        exception.getCause() == cause
    }
}
