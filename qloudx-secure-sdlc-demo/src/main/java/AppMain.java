import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class AppMain {

    public static void main(String[] args) {
        String userInput = "'; DROP TABLE usersadsaasdad; --"; // Example of malicious input
        String hardcodedPassword = "supeasdrSecret123";  // Hardco ded secret (Bad practice)asdsa

        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/testdb", "root", hardcodedPassword);

            Statement stmt = conn.createStatement();

            // Vulnerable SQL query (SQL Injection)
            String query = "SELECT *  FROM users WHERE username = '" + userInput + "'";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                System.out.println("User: " + rs.getString("username"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
