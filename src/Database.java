import java.sql.*;
import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

public class Database {

    public static Connection connect() throws SQLException {
        return DBConnection.getConnection();
    }

    // Create all tables
    public static void createTable() {

        String itemsSQL = """
            CREATE TABLE IF NOT EXISTS items (
                id INT AUTO_INCREMENT PRIMARY KEY,
                item_name VARCHAR(100) NOT NULL,
                category VARCHAR(50),
                location VARCHAR(150),
                report_type VARCHAR(10),
                status VARCHAR(20) DEFAULT 'Open',
                reported_date DATE,
                contact VARCHAR(30),
                image_path VARCHAR(500)
            )
        """;

        String usersSQL = """
            CREATE TABLE IF NOT EXISTS users (
                user_id INT AUTO_INCREMENT PRIMARY KEY,
                full_name VARCHAR(100) NOT NULL,
                student_id VARCHAR(50) NOT NULL UNIQUE,
                email VARCHAR(150) NOT NULL UNIQUE,
                phone VARCHAR(15) NOT NULL,
                password_hash VARCHAR(255) NOT NULL,
                role ENUM('STUDENT','ADMIN') DEFAULT 'STUDENT',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (Connection con = connect();
             Statement st = con.createStatement()) {

            st.executeUpdate(itemsSQL);
            st.executeUpdate(usersSQL);

            System.out.println("Database Tables Ready!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Password hashing
    private static String hashPassword(String password)
            throws Exception {

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(
            password.toCharArray(), salt, 65536, 256
        );

        SecretKeyFactory factory =
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hash = factory.generateSecret(spec).getEncoded();

        spec.clearPassword();

        return Base64.getEncoder().encodeToString(salt)
                + ":" +
                Base64.getEncoder().encodeToString(hash);
    }

    // Verify password
    private static boolean verifyPassword(
            String password, String storedHash) throws Exception {

        String[] parts = storedHash.split(":");

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] originalHash = Base64.getDecoder().decode(parts[1]);

        PBEKeySpec spec = new PBEKeySpec(
            password.toCharArray(), salt, 65536, 256
        );

        SecretKeyFactory factory =
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] newHash = factory.generateSecret(spec).getEncoded();

        spec.clearPassword();

        return java.security.MessageDigest.isEqual(
            originalHash, newHash
        );
    }

    // Student Registration
    public static boolean registerUser(
            String name,
            String studentId,
            String email,
            String phone,
            String password) throws Exception {

        String sql = """
            INSERT INTO users
            (full_name, student_id, email, phone, password_hash, role)
            VALUES (?, ?, ?, ?, ?, 'STUDENT')
        """;

        String hashedPassword = hashPassword(password);

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, studentId);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, hashedPassword);

            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        }
    }

// Create Admin Account
public static boolean registerAdmin(
        String name,
        String staffId,
        String email,
        String phone,
        String password) throws Exception {

    String sql = """
        INSERT INTO users
        (full_name, student_id, email, phone, password_hash, role)
        VALUES (?, ?, ?, ?, ?, 'ADMIN')
    """;

    String hashedPassword = hashPassword(password);

    try (
        Connection con = connect();
        PreparedStatement ps = con.prepareStatement(sql)
    ) {

        ps.setString(1, name);
        ps.setString(2, staffId);
        ps.setString(3, email);
        ps.setString(4, phone);
        ps.setString(5, hashedPassword);

        return ps.executeUpdate() > 0;

    } catch (SQLIntegrityConstraintViolationException e) {

        return false;
    }
}
    // Student/Admin Login
    public static ResultSet loginUser(
            String email, String password) throws Exception {

        String sql = """
            SELECT * FROM users WHERE email = ?
        """;

        Connection con = connect();

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, email);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            String storedHash = rs.getString("password_hash");

            if (verifyPassword(password, storedHash)) {
                return rs;
            }
        }

        rs.close();
        ps.close();
        con.close();

        return null;
    }
    public static String[] authenticateUser(
        String email, String password) throws Exception {

    String sql = "SELECT * FROM users WHERE email = ?";

    try (Connection con = connect();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, email);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                String storedHash = rs.getString("password_hash");

                if (verifyPassword(password, storedHash)) {

                    return new String[]{
                        rs.getString("user_id"),
                        rs.getString("full_name"),
                        rs.getString("role")
                    };
                }
            }
        }
    }

    return null;
}

    // Add item
    public static int addItem(
            String name, String category, String location,
            String type, String contact, String imagePath)
            throws SQLException {

        String sql = """
            INSERT INTO items
            (item_name, category, location, report_type,
             status, reported_date, contact, image_path)
            VALUES (?, ?, ?, ?, 'Open', CURDATE(), ?, ?)
        """;

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, category);
            ps.setString(3, location);
            ps.setString(4, type);
            ps.setString(5, contact);
            ps.setString(6, imagePath);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return -1;
    }
    public static int addStudentItem(
        int userId,
        String name,
        String category,
        String description,
        String location,
        String type,
        String contact,
        String imagePath) throws SQLException {

    String sql = """
        INSERT INTO items
        (user_id, item_name, category, description,
         location, report_type, status, reported_date,
         contact, image_path)
        VALUES (?, ?, ?, ?, ?, ?, 'Open', CURDATE(), ?, ?)
    """;

    try (Connection con = connect();
         PreparedStatement ps = con.prepareStatement(
             sql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setInt(1, userId);
        ps.setString(2, name);
        ps.setString(3, category);
        ps.setString(4, description);
        ps.setString(5, location);
        ps.setString(6, type);
        ps.setString(7, contact);
        ps.setString(8, imagePath);

        ps.executeUpdate();

        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
    }

    return -1;
}

    // Get all items
    public static ResultSet getItems(Connection con)
            throws SQLException {

        String sql = "SELECT * FROM items ORDER BY id DESC";

        PreparedStatement ps = con.prepareStatement(sql);
        return ps.executeQuery();
    }

    // Update item
    public static void updateItem(
            int id, String name, String category,
            String location, String type,
            String contact, String imagePath)
            throws SQLException {

        String sql = """
            UPDATE items
            SET item_name = ?, category = ?,
                location = ?, report_type = ?,
                contact = ?, image_path = ?
            WHERE id = ?
        """;

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, category);
            ps.setString(3, location);
            ps.setString(4, type);
            ps.setString(5, contact);
            ps.setString(6, imagePath);
            ps.setInt(7, id);

            ps.executeUpdate();
        }
    }

    // Delete item
    public static void deleteItem(int id) throws SQLException {

        String sql = "DELETE FROM items WHERE id = ?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Mark as returned
    public static void markReturned(int id) throws SQLException {

        String sql =
            "UPDATE items SET status = 'Returned' WHERE id = ?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    
    public static String[] getUserDetails(int userId) throws Exception {

    String sql = "SELECT user_id, full_name, student_id, email, phone "
               + "FROM users WHERE user_id = ?";

    try (Connection con = connect();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, userId);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new String[]{
                    rs.getString("user_id"),
                    rs.getString("full_name"),
                    rs.getString("student_id"),
                    rs.getString("email"),
                    rs.getString("phone")
                };
            }
        }
    }

    return null;
}
public static ResultSet getMyReports(Connection con, int userId)
        throws SQLException {

    String sql = """
        SELECT id, item_name, category, location,
               report_type, status, reported_date
        FROM items
        WHERE user_id = ?
        ORDER BY id DESC
    """;

    PreparedStatement ps = con.prepareStatement(sql);
    ps.setInt(1, userId);

    return ps.executeQuery();
}
// Submit Claim. Proof photo is optional; only administrators can review it.
public static boolean submitClaim(
        int itemId, int studentId, String description
) throws SQLException {
    return submitClaim(itemId, studentId, description, null);
}

public static boolean submitClaim(
        int itemId, int studentId, String description, String proofImagePath
) throws SQLException {

    String sql = """
        INSERT INTO claims
        (item_id, student_id, claim_description, status, proof_image_path)
        SELECT ?, ?, ?, 'Pending', ?
        FROM items
        WHERE id = ?
        AND report_type = 'Found'
        AND status = 'Open'
        AND user_id IS NOT NULL
        AND user_id <> ?
        AND NOT EXISTS (
            SELECT 1 FROM claims
            WHERE item_id = ? AND student_id = ?
        )
    """;

    try (Connection con = connect();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, itemId);
        ps.setInt(2, studentId);
        ps.setString(3, description);
        ps.setString(4, proofImagePath);
        ps.setInt(5, itemId);
        ps.setInt(6, studentId);
        ps.setInt(7, itemId);
        ps.setInt(8, studentId);

        int inserted = ps.executeUpdate();
        if (inserted > 0) {
            try (PreparedStatement owner = con.prepareStatement(
                    "SELECT user_id FROM items WHERE id=?")) {
                owner.setInt(1, itemId);
                try (ResultSet rs = owner.executeQuery()) {
                    if (rs.next() && rs.getObject(1) != null) {
                        ProjectEnhancements.notifyUser(con, rs.getInt(1),
                            "A student submitted a claim for your found item #" + itemId + ".");
                    }
                }
            }
            try (PreparedStatement history = con.prepareStatement(
                    "INSERT INTO item_history(item_id,changed_by,action,details) VALUES(?,?,?,?)")) {
                history.setInt(1, itemId);
                history.setInt(2, studentId);
                history.setString(3, "Claim submitted");
                history.setString(4, "A student submitted a claim for this item.");
                history.executeUpdate();
            }
        }
        return inserted > 0;
    }
}

public static void main(String[] args) {
        createTable();
    }
}