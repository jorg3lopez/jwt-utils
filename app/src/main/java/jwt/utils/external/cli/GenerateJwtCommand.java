package jwt.utils.external.cli;

import jwt.utils.external.Jjwt;
import jwt.utils.wrappers.TokenGenerationException;
import org.w3c.dom.css.CSSImportRule;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Command(name = "generate-jwt", description = "Generate a JSON Token (JWT)")
public class GenerateJwtCommand implements Runnable {

    private final String SIMULATED_HOSPITAL = "flexion.simulated-hospital";
    private final String ETOR_SERVICE_SENDER = "flexion.etor-service-sender";
    private final String RS_STAGING = "staging.prime.cdc.gov";

    @Option(names = {"-p", "--privatekey"}, description = "Path to the private key file", required = true)
    private String privateKeyPath;

    @Option(names = {"-o", "--output"}, description = "Path to the output file")
    private String outputPath;

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display help information")
    private boolean helpRequested;

    @Option(names = {"-kid", "--keyId"}, description = "Key ID for JWT header", defaultValue = SIMULATED_HOSPITAL)
    private String keyId;

    @Option(names = {"-i", "--issuer"}, description = "Issuer for JWT claims", defaultValue = SIMULATED_HOSPITAL)
    private String issuer;

    @Option(names = {"-s", "--subject"}, description = "Subject for JWT claims", defaultValue = SIMULATED_HOSPITAL)
    private String subject;

    @Option(names = {"-a", "--audience"}, description = "Audience for JWT claims", defaultValue = RS_STAGING)
    private String audience;

    @Override
    public void run() {
        if (helpRequested) {
            CommandLine.usage(this, System.out);
            return;
        }
        // Load the private key from the specific path
        try {
            String privateKey = Files.readString(Paths.get(privateKeyPath));
            Jjwt jwtUtils = new Jjwt();
            String jwt = jwtUtils.generateToken(keyId,issuer,subject,audience,300,privateKey);
            System.out.println("\n************************************jwt");
            System.out.println(jwt);
            System.out.println("**************************************end-jwt");
        } catch (IOException | TokenGenerationException e) {
            throw new RuntimeException(e);
        }
    }
}
