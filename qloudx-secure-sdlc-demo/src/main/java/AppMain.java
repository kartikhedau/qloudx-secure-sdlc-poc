import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class AppMain {

    public static void main(String[] args) {
        try {
            // 1. Hardcoded AWS credentials (Bad practice)
            String awsAccessKeyId = "AKIA1234567890ABCD"; // AWS Access Key ID
            String awsSecretAccessKey = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"; // AWS Secret Access Key

            System.out.println("Connecting to AWS with Access Key: " + awsAccessKeyId);

            // ** AWS S3 client using hardcoded credentials **
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                    .withRegion("us-east-1")
                    .build();

            // Try listing buckets (simulation)
            System.out.println("Listing S3 Buckets.ada..");
            s3Client.listBuckets().forEach(bucket -> System.out.println("Bucket: " + bucket.getName()));

            // 2. SQL Injection vulnerability
            String userInput = "'; DROP TABLE users; --"; // Malicious input
            String query = "SELECT * FROM users WHERE username = '" + userInput + "'";

            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "root", "asd");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query); // Vulnerable to SQL Injection

                while (rs.next()) {
                    System.out.println("User: " + rs.getString("username"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // 3. Insecure Random for security token
            Random random = new Random();
            int token = random.nextInt();
            System.out.println("Generated token: " + token);

            // 4. Command Injection
            String cmdInput = "ls; rm -rf /"; // Simulated malicious input
            Runtime.getRuntime().exec("sh -c " + cmdInput);

            // 5. Insecure Deserialization
            String maliciousData = Base64.getEncoder().encodeToString("rO0ABXNy...".getBytes());
            byte[] data = Base64.getDecoder().decode(maliciousData);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object obj = ois.readObject();
            System.out.println("Deserialized object: " + obj);

            // 6. Disable SSL certificate validation
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

            // 7. Insecure HTTP (no HTTPS)
            URL url = new URL("http://example.com");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            System.out.println("HTTP Response: " + conn.getResponseCode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
