package jwt.utils.external.cli

import picocli.CommandLine
import spock.lang.Specification

class MainCliTest extends Specification {
    def "short-hand options work"() {
        given:
        def args = ["-h"]
        def mainCli = new MainCli()

        when:
        new CommandLine(mainCli).parseArgs(args as String[])

        then:
        mainCli.helpRequested
    }

    def "long-hand options work"() {
        given:
        def args = ["--help"]
        def mainCli = new MainCli()

        when:
        new CommandLine(mainCli).parseArgs(args as String[])

        then:
        mainCli.helpRequested
    }
}
