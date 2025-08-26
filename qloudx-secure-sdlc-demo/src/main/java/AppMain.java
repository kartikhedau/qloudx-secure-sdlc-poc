import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;

public class AppMain {

    public static void main(String[] args) {
        try {
            // 1. Hardcoded credentials (Bad practice) 
            String hardcodedPassword = "SuperSecret123!";
            String apiKey = "API-KEY-12345";

            // 2. Logging sensitive information
            System.out.println("API Key: " + apiKey);
            System.out.println("Password: " + hardcodedPassword);

            // 3. SQL Injection
            String userInput = "'; DROP TABLE users; --";
            String query = "SELECT * FROM users WHERE username = '" + userInput + "'";
            System.out.println("Executing query: " + query);

            // 4. Insecure Random for security token
            Random random = new Random();
            int token = random.nextInt();
            System.out.println("Generated token: " + token);

            // 5. Command Injection
            String cmdInput = "ls; rm -rf /"; // Simulated malicious input
            Runtime.getRuntime().exec("sh -c " + cmdInput);

            // 6. Insecure Deserialization
            String maliciousData = Base64.getEncoder().encodeToString("rO0ABXNy...".getBytes());
            byte[] data = Base64.getDecoder().decode(maliciousData);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object obj = ois.readObject();
            System.out.println("Deserialized object: " + obj);

            // 7. Disable SSL certificate validation
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            // 8. Insecure HTTP (no HTTPS)
            URL url = new URL("http://example.com"); // Insecure URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            System.out.println("HTTP Response: " + conn.getResponseCode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
