package jwt.utils.external.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


@Command(name = "ti", subcommands = GenerateJwtCommand.class, description = "A command-line tool")
public class MainCli implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display help information")
    private boolean helpRequested;

    @Override
    public void run() {
        if (helpRequested) {
            CommandLine.usage(this, System.out);
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new MainCli()).execute(args);
        System.exit(exitCode);
    }


}
