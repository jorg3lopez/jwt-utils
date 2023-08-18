package jwt.utils

import jwt.utils.external.Jjwt
import jwt.utils.wrappers.TokenGenerationException
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit


class JjwtTest extends Specification {

    def "readPrivateKey works"() {
        given:
        def expected = """SunRsaSign RSA private CRT key, 2048 bits
  params: null
  modulus: 19528401785820274642298200447198693681372683818166413421385736601141636263840673511780857948065066192425480740569384065251977984752274011588349603209362220858474144363269608864544021309243923425464674153672851212250807075333774507183934992538194604415871746843975421033925806033725682694268564837941696215704563271984695037424643589594315690582441585451087124310764173323174976196329306739146932994562903732368923974808443398706152297094266693501167626217092999366858772558067591373600281117715445851609266122297286134574031931957290645285895752099696938240772735283025327358171112881542098865271180836995981447898543
  private exponent: 1910766077964699500186722162559494991101245631994020569520057920790099371986485785041957239299437523214800727053438139042499791373177855246811600021057345335535124444656703340767635635526903260231506218020769311969701090537830981389398097863057096004040123542990866035675255675286722618932773426054001968097467312259661106947832982999928248710074411857380750542921736381217021390714463044149444338915835812058875567805475585617478083362593774328408377385164105951793256490444260459262212275582290389836055024046342652289520943197538224770989856639834489941439382107055597634068387996679058256435389879776212248564973"""

        def key = Files.readString(Path.of("..", "mock_credentials", "private-key.pem"))

        when:
        def actual = Jjwt.getInstance().readPrivateKey(key)

        then:
        actual.toString() == expected
    }

    def "generateToken blows up because the key isn't Base64"() {
        given:
        def key = "DogCow is not actually an Base64 encoded"

        when:
        Jjwt.getInstance().generateToken("keyId", "issuer", "subject", "audience", 300, key)

        then:
        thrown(TokenGenerationException)
    }

    def "generateToken blows up because the key isn't valid RSA"() {
        given:
        def key = Base64.getEncoder().encodeToString("DogCow is not actually an RSA key".getBytes(StandardCharsets.UTF_8))

        when:
        Jjwt.getInstance().generateToken("keyId", "issuer", "subject", "audience", 300, key)

        then:
        thrown(TokenGenerationException)
    }

    def "generateToken blows up because Jjwt doesn't like something"() {
        given:
        def key = Files.readString(Path.of("..", "mock_credentials", "weak-rsa-key.pem"))

        when:
        Jjwt.getInstance().generateToken("keyId", "issuer", "subject", "audience", 300, key)

        then:
        thrown(TokenGenerationException)
    }

    def "getExpirationDate works with unexpired JWT"() {
        given:
        def pemKey = Files.readString(Path.of("..", "mock_credentials", "private-key.pem"))
        def secondsFromNowExpiration = 300
        def expectedExpirationCenter = LocalDateTime.now().plusSeconds(secondsFromNowExpiration).truncatedTo(ChronoUnit.SECONDS)
        def expectedExpirationUpper = expectedExpirationCenter.plusSeconds(3)  // 3 seconds is the window in which we expect the the code below to execute to generate the JWT (which is very generous, it should take much shorter than this)

        def jwt = Jjwt.getInstance().generateToken("DogCow", "Dogcow", "subject", "fake_URL", secondsFromNowExpiration, pemKey)

        when:
        def actualExpiration = Jjwt.getInstance().getExpirationDate(jwt)

        then:
        //testing that the actual expiration is between (inclusive) the expectedExpirationCenter and expectedExpirationUpper time
        (actualExpiration.isEqual(expectedExpirationCenter) || actualExpiration.isAfter(expectedExpirationCenter)) && (actualExpiration.isEqual(expectedExpirationUpper) || actualExpiration.isBefore(expectedExpirationUpper))
    }

    def "getExpirationDate works when the JWT is expired"() {
        given:
        def fileName = "expired-token"
        def expiredToken = Files.readString(Path.of("..", "mock_credentials", fileName + ".jwt"))

        when:
        def actual = Jjwt.getInstance().getExpirationDate(expiredToken)
        def expected = LocalDateTime.ofInstant(Instant.ofEpochSecond(1692376796L), ZoneId.systemDefault())

        then:
        actual == expected
    }

    def "readKey correctly reads a private key"() {
        given:
        def privateKeyString = Files.readString(Path.of("..", "mock_credentials", "private-key.pem"))

        when:
        def key = Jjwt.getInstance().readKey(privateKeyString)

        then:
        key != null
    }

    def "readKey correctly reads a public key"() {
        given:
        def publicKeyString = Files.readString(Path.of("..", "mock_credentials", "public-key.pem"))

        when:
        def key = Jjwt.getInstance().readKey(publicKeyString)

        then:
        key != null
    }
}
