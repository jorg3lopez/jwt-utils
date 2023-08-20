package jwt.utils.external.cli

import picocli.CommandLine
import spock.lang.Specification


class GenerateJwtCommandTest extends Specification {

    def "options with valid input"() {
        given:
        def privateKeyPath = "../mock_credentials/private-key.pem"
        def keyId = "myKeyId"
        def issuer = "myIssuer"
        def subject = "mySubject"
        def audience = "myAudience"
        def helpRequested = false
        def cmd = new GenerateJwtCommand()
        def cmdLine = new CommandLine(cmd)

        when:
        def args = ["-p", privateKeyPath, "--keyId", keyId, "--issuer", issuer, "--subject", subject, "--audience", audience]
        cmdLine.parseArgs(args as String[])
        cmd.run()

        then:
        cmd.privateKeyPath == privateKeyPath
        cmd.keyId == keyId
        cmd.issuer == issuer
        cmd.subject == subject
        cmd.audience == audience
        cmd.helpRequested == helpRequested
    }

    def "--help option works"() {
        given:
        def args = ["--help"]
        def cmd = new GenerateJwtCommand()
        def cmdLine = new CommandLine(cmd)

        when:
        cmdLine.parseArgs(args as String[])
        cmd.run()

        then:
        cmd.helpRequested
    }

    def "invalid privateKeyPath input"() {
        given:
        def privateKeyPath = "invalid-path.pem"
        def args = ["--privateKeyPath", privateKeyPath]
        def cmd = new GenerateJwtCommand()
        def cmdLine = new CommandLine(cmd)

        when:
        cmdLine.parseArgs(args as String[])
        cmd.run()

        then:
        thrown(RuntimeException)
    }
    
}
