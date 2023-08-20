package jwt.utils.external.cli;

import jwt.utils.external.Jjwt;
import jwt.utils.wrappers.TokenGenerationException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Command(name = "generate-jwt", description = "Generate a JSON Token (JWT)")
public class GenerateJwtCommand implements Runnable {

    private final String SIMULATED_HOSPITAL = "flexion.simulated-hospital";
    private final String ETOR_SERVICE_SENDER = "flexion.etor-service-sender";
    private final String RS_STAGING = "staging.prime.cdc.gov";

    @Option(names = {"-p", "--privateKeyPath"}, description = "Path to the private key file", required = true)
    protected String privateKeyPath;

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display help information")
    protected boolean helpRequested;

    @Option(names = {"-kid", "--keyId"}, description = "Key ID for JWT header", defaultValue = ETOR_SERVICE_SENDER)
    protected String keyId;

    @Option(names = {"-i", "--issuer"}, description = "Issuer for JWT claims", defaultValue = ETOR_SERVICE_SENDER)
    protected String issuer;

    @Option(names = {"-s", "--subject"}, description = "Subject for JWT claims", defaultValue = ETOR_SERVICE_SENDER)
    protected String subject;

    @Option(names = {"-a", "--audience"}, description = "Audience for JWT claims", defaultValue = RS_STAGING)
    protected String audience;

    @Override
    public void run() {
        if (helpRequested) {
            CommandLine.usage(this, System.out);
            return;
        }
        String jwt;
        // Load the private key from the specific path
        try {
            String privateKey = Files.readString(Paths.get(privateKeyPath));
            Jjwt jwtUtils = new Jjwt();
            jwt = jwtUtils.generateToken(keyId, issuer, subject, audience, 300, privateKey);
            System.out.println("\n******************************************jwt");
            System.out.println(jwt);
            System.out.println("**************************************end-jwt\n");
        } catch (IOException | TokenGenerationException e) {
            throw new RuntimeException(e);
        }

        // Write the JWT to the output file
        createFile(jwt);
            
        
    }

    private void createFile(String jwt) {
        try {
            // Create the 'build/jwt' directory if it doesn't exist
            Path jwtDir = Paths.get("app/build/jwt");
            Files.createDirectories(jwtDir);

            String filename = generateFilename();
            
            // Write the JWT to the 'build/jwt/generatedJWT.jwt' file
            Path outputPath = jwtDir.resolve(filename);
            Files.write(outputPath, jwt.getBytes());
            
            System.out.println("JWT generated and saved to: app/build/jwt/" + filename);
        } catch (IOException e) {
            System.out.println("unable to write JWT to file.\n" + e.getMessage());
        }
    }

    protected String generateFilename() {
        // Generate a unique filename based on timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());

        return "jwt_" + timestamp + ".jwt";
    }
}
