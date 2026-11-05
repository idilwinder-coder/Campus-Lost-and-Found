import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/campus_lost_found";

    private static final String USER = "root";

    // Apna actual MySQL password yahan likhna
    private static final String PASSWORD = "CHANGE_ME";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection con = getConnection()) {
            if (con.isValid(2)) {
                System.out.println("MySQL Connected Successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Connection Failed!");
            System.out.println(e.getMessage());
        }
    }
}