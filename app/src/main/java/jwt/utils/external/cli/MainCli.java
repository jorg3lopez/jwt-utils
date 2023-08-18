package jwt.utils.external.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.Arrays;

public class MainCli implements Runnable {

    @Option(names = {"--privatekey"}, description = "Path to the private key file")
    private String privateKeyPath;

    @Option(names = {"--output"}, description = "Path to the output file")
    private String outputPath;

    @Override
    public void run() {
        System.out.println("This is the main command");
        System.out.println("testing the parameters...");
        System.out.println("Private key path: " + privateKeyPath);
        System.out.println("Output path: " + outputPath);
    }

    public static void main(String[] args) {
        Arrays.stream(args).forEach(System.out::println);
        // CommandLine.run(new MainCli(), args);
        int exitCode = new CommandLine(new MainCli()).execute(args);
        System.exit(exitCode);
    }


}
