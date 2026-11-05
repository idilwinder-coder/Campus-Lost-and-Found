import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.sql.*;

public class AdminDashboard extends JFrame {

private final Color blue = new Color(35, 91, 190);
private final Color darkBlue = new Color(24, 53, 105);
private final Color background = new Color(244, 247, 252);

private DefaultTableModel itemModel, claimModel, studentModel;

private JTable itemTable, claimTable, studentTable;

private JLabel studentsLabel, itemsLabel, lostLabel, foundLabel, claimsLabel;

private JTextField studentSearchField;

private JTabbedPane tabs;

public AdminDashboard() {
    UITheme.apply();

    setTitle("Campus Lost & Found - Admin Panel");
    setSize(1150, 700);
    setMinimumSize(new Dimension(950, 600));
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel main = new JPanel(new BorderLayout());
    main.setBackground(background);

    // SIDEBAR
    JPanel sidebar = new JPanel();
    sidebar.setBackground(darkBlue);
    sidebar.setPreferredSize(new Dimension(220, 0));
    sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

    JLabel logo = new JLabel("ADMIN PANEL");
    logo.setForeground(Color.WHITE);
    logo.setFont(new Font("Arial", Font.BOLD, 23));
    logo.setBorder(BorderFactory.createEmptyBorder(30, 20, 40, 10));

    sidebar.add(logo);

    JButton dashboardButton = menuButton("Dashboard");
    JButton itemsButton = menuButton("Manage Items");
    JButton claimsButton = menuButton("Manage Claims");
    JButton studentsButton = menuButton("Manage Students");
    JButton refreshButton = menuButton("Refresh Data");

    sidebar.add(dashboardButton);
    sidebar.add(itemsButton);
    sidebar.add(claimsButton);
    sidebar.add(studentsButton);
    sidebar.add(refreshButton);

    JButton analyticsButton = menuButton("Monthly Analytics");
    JButton historyButton = menuButton("Item History");
    JButton notificationButton = menuButton("Notifications");
    sidebar.add(analyticsButton);
    sidebar.add(historyButton);
    sidebar.add(notificationButton);
    analyticsButton.addActionListener(e -> ProjectEnhancements.openAnalytics(this));
    historyButton.addActionListener(e -> ProjectEnhancements.openHistory(this));
    notificationButton.addActionListener(e -> ProjectEnhancements.showQuery(this, "All Notifications", "SELECT n.notification_id AS ID,u.full_name AS User,n.message AS Message,n.created_at AS Date FROM notifications n LEFT JOIN users u ON u.user_id=n.user_id ORDER BY n.notification_id DESC"));

    sidebar.add(Box.createVerticalGlue());

    JButton logoutButton = menuButton("Logout");
    sidebar.add(logoutButton);

    // CONTENT
    JPanel content = new JPanel(new BorderLayout(15, 15));
    content.setBackground(background);
    content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel heading = new JLabel("Admin Dashboard");
    heading.setFont(new Font("Arial", Font.BOLD, 28));
    heading.setForeground(darkBlue);

    // STATISTICS
    JPanel statsPanel = new JPanel(new GridLayout(1, 5, 12, 12));
    statsPanel.setBackground(background);

    studentsLabel = new JLabel("0");
    itemsLabel = new JLabel("0");
    lostLabel = new JLabel("0");
    foundLabel = new JLabel("0");
    claimsLabel = new JLabel("0");

    statsPanel.add(createStatCard("Students", studentsLabel));
    statsPanel.add(createStatCard("Total Items", itemsLabel));
    statsPanel.add(createStatCard("Lost Items", lostLabel));
    statsPanel.add(createStatCard("Found Items", foundLabel));
    statsPanel.add(createStatCard("Pending Claims", claimsLabel));

    // ITEM TABLE
    itemModel = new DefaultTableModel(
        new String[]{"ID", "Item Name", "Category", "Location",
            "Type", "Status", "Date", "Contact"}, 0
    ) {
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    itemTable = new JTable(itemModel);
    itemTable.setRowHeight(28);

    // CLAIM TABLE
    claimModel = new DefaultTableModel(
        new String[]{"Claim ID", "Item ID", "Item Name",
            "Student ID", "Description", "Status", "Date", "Proof Photo"}, 0
    ) {
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    claimTable = new JTable(claimModel);
    claimTable.setRowHeight(28);
    claimTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    claimTable.setToolTipText("Double-click a claim or select it and choose View Details to inspect the student's information and photos.");
    claimTable.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override public void mouseClicked(java.awt.event.MouseEvent e) {
            if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                showClaimDetails();
            }
        }
    });

    // STUDENT TABLE
    studentModel = new DefaultTableModel(
        new String[]{"User ID", "Name", "Student ID",
            "Email", "Phone", "Registration Date"}, 0
    ) {
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    studentTable = new JTable(studentModel);
    studentTable.setRowHeight(28);

    // TABS
    tabs = new JTabbedPane();

    tabs.addTab("Manage Items", new JScrollPane(itemTable));

    // CLAIM TAB
    JPanel claimPanel = new JPanel(new BorderLayout());
    claimPanel.add(new JScrollPane(claimTable), BorderLayout.CENTER);

    JPanel claimButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    JButton detailsButton = new JButton("View Claim Details");
    JButton approveButton = new JButton("Approve Claim");
    JButton rejectButton = new JButton("Reject Claim");

    UITheme.styleSecondaryButton(detailsButton);
    UITheme.stylePrimaryButton(approveButton);
    UITheme.stylePrimaryButton(rejectButton);
    approveButton.setBackground(UITheme.SUCCESS);
    rejectButton.setBackground(new Color(174, 70, 70));

    claimButtons.add(detailsButton);
    claimButtons.add(approveButton);
    claimButtons.add(rejectButton);

    claimPanel.add(claimButtons, BorderLayout.SOUTH);

    tabs.addTab("Manage Claims", claimPanel);

    // STUDENT TAB
    JPanel studentPanel = new JPanel(new BorderLayout(10, 10));
    studentPanel.setBackground(Color.WHITE);
    studentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    searchPanel.setBackground(Color.WHITE);

    studentSearchField = new JTextField(25);

    JButton searchButton = new JButton("Search");
    JButton clearButton = new JButton("Clear");

    searchPanel.add(new JLabel("Search Student:"));
    searchPanel.add(studentSearchField);
    searchPanel.add(searchButton);
    searchPanel.add(clearButton);

    studentPanel.add(searchPanel, BorderLayout.NORTH);
    studentPanel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

    tabs.addTab("Manage Students", studentPanel);

    // CENTER PANEL
    JPanel center = new JPanel(new BorderLayout(10, 15));
    center.setBackground(background);

    center.add(statsPanel, BorderLayout.NORTH);
    center.add(tabs, BorderLayout.CENTER);

    content.add(heading, BorderLayout.NORTH);
    content.add(center, BorderLayout.CENTER);

    main.add(sidebar, BorderLayout.WEST);
    main.add(content, BorderLayout.CENTER);

    add(main);

    // BUTTON ACTIONS
    dashboardButton.addActionListener(e -> tabs.setSelectedIndex(0));
    itemsButton.addActionListener(e -> tabs.setSelectedIndex(0));
    claimsButton.addActionListener(e -> tabs.setSelectedIndex(1));
    studentsButton.addActionListener(e -> {
        tabs.setSelectedIndex(2);
        loadStudents("");
    });

    refreshButton.addActionListener(e -> loadAllData());

    searchButton.addActionListener(e ->
        loadStudents(studentSearchField.getText().trim())
    );

    studentSearchField.addActionListener(e ->
        loadStudents(studentSearchField.getText().trim())
    );

    clearButton.addActionListener(e -> {
        studentSearchField.setText("");
        loadStudents("");
    });

    detailsButton.addActionListener(e -> showClaimDetails());
    approveButton.addActionListener(e -> updateClaim("Approved"));
    rejectButton.addActionListener(e -> updateClaim("Rejected"));

    logoutButton.addActionListener(e -> {
        dispose();
        new LoginFrame();
    });

    loadAllData();

    UITheme.styleWindow(this);
    setVisible(true);
}

// SIDEBAR BUTTON
private JButton menuButton(String text) {

    JButton button = new JButton(text);

    button.setMaximumSize(new Dimension(200, 45));
    button.setForeground(Color.WHITE);
    button.setBackground(darkBlue);
    button.setFont(new Font("Arial", Font.BOLD, 14));
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setHorizontalAlignment(SwingConstants.LEFT);

    return button;
}

// STATISTICS CARD
private JPanel createStatCard(String title, JLabel value) {

    JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
    panel.setBackground(Color.WHITE);

    panel.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 235)),
            BorderFactory.createEmptyBorder(15, 12, 15, 12)
        )
    );

    JLabel titleLabel = new JLabel(title);
    titleLabel.setForeground(Color.GRAY);
    titleLabel.setFont(new Font("Arial", Font.PLAIN, 13));

    value.setForeground(blue);
    value.setFont(new Font("Arial", Font.BOLD, 25));

    panel.add(titleLabel);
    panel.add(value);

    return panel;
}

// LOAD ALL DATA
private void loadAllData() {
    loadStatistics();
    loadItems();
    loadClaims();
    loadStudents(studentSearchField == null ? "" : studentSearchField.getText().trim());
}

// LOAD STATISTICS
private void loadStatistics() {

    try (Connection con = Database.connect()) {

        studentsLabel.setText(
            getCount(con, "SELECT COUNT(*) FROM users WHERE role='STUDENT'")
        );

        itemsLabel.setText(getCount(con, "SELECT COUNT(*) FROM items"));

        lostLabel.setText(
            getCount(con, "SELECT COUNT(*) FROM items WHERE report_type='Lost'")
        );

        foundLabel.setText(
            getCount(con, "SELECT COUNT(*) FROM items WHERE report_type='Found'")
        );

        claimsLabel.setText(
            getCount(con, "SELECT COUNT(*) FROM claims WHERE status='Pending'")
        );

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Unable to load statistics.");
    }
}

private String getCount(Connection con, String sql) throws SQLException {

    try (
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql)
    ) {
        if (rs.next()) return rs.getString(1);
    }

    return "0";
}

// LOAD ITEMS
private void loadItems() {

    itemModel.setRowCount(0);

    String sql = """
        SELECT id, item_name, category, location,
               report_type, status, reported_date, contact
        FROM items
        ORDER BY id DESC
    """;

    try (
        Connection con = Database.connect();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()
    ) {

        while (rs.next()) {
            itemModel.addRow(new Object[]{
                rs.getInt("id"),
                rs.getString("item_name"),
                rs.getString("category"),
                rs.getString("location"),
                rs.getString("report_type"),
                rs.getString("status"),
                rs.getString("reported_date"),
                rs.getString("contact")
            });
        }

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Unable to load items.");
    }
}

// LOAD CLAIMS
private void loadClaims() {

    claimModel.setRowCount(0);

    String sql = """
        SELECT c.claim_id, c.item_id, i.item_name,
               c.student_id, c.claim_description,
               c.status, c.claim_date,
               CASE WHEN c.proof_image_path IS NULL OR c.proof_image_path = ''
                    THEN 'Not attached' ELSE 'Attached' END AS proof_photo
        FROM claims c
        LEFT JOIN items i ON c.item_id = i.id
        ORDER BY c.claim_id DESC
    """;

    try (
        Connection con = Database.connect();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()
    ) {

        while (rs.next()) {
            claimModel.addRow(new Object[]{
                rs.getInt("claim_id"),
                rs.getInt("item_id"),
                rs.getString("item_name"),
                rs.getInt("student_id"),
                rs.getString("claim_description"),
                rs.getString("status"),
                rs.getString("claim_date"),
                rs.getString("proof_photo")
            });
        }

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Unable to load claims.");
    }
}

// LOAD AND SEARCH STUDENTS
private void loadStudents(String keyword) {

studentModel.setRowCount(0);

String sql = """
    SELECT user_id, full_name, student_id, email, phone, created_at
    FROM users
    WHERE role = 'STUDENT'
    ORDER BY user_id DESC
""";

try (
    Connection con = Database.connect();
    PreparedStatement ps = con.prepareStatement(sql);
    ResultSet rs = ps.executeQuery()
) {

    String search = keyword.trim().toLowerCase();

    while (rs.next()) {

        String name = rs.getString("full_name");
        String studentId = rs.getString("student_id");
        String email = rs.getString("email");
        String phone = rs.getString("phone");

        boolean match = search.isEmpty()
            || (name != null && name.toLowerCase().contains(search))
            || (studentId != null && studentId.toLowerCase().contains(search))
            || (email != null && email.toLowerCase().contains(search))
            || (phone != null && phone.toLowerCase().contains(search));

        if (match) {
            studentModel.addRow(new Object[]{
                rs.getInt("user_id"),
                name,
                studentId,
                email,
                phone,
                rs.getString("created_at")
            });
        }
    }

    System.out.println("Matching students: " + studentModel.getRowCount());

} catch (Exception ex) {
    ex.printStackTrace();
    JOptionPane.showMessageDialog(this, ex.getMessage());
}


}



// ADMIN CLAIM REVIEW: show claimant details, item details and both available photos.
private void showClaimDetails() {
    int selectedRow = claimTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Select a claim to review first.");
        return;
    }

    int modelRow = claimTable.convertRowIndexToModel(selectedRow);
    int claimId = Integer.parseInt(claimModel.getValueAt(modelRow, 0).toString());

    String sql = """
        SELECT c.claim_id, c.item_id, c.student_id, c.claim_description,
               c.status AS claim_status, c.claim_date, c.proof_image_path,
               u.full_name AS claimant_name, u.student_id AS claimant_student_id,
               u.email AS claimant_email, u.phone AS claimant_phone,
               i.item_name, i.category, i.location, i.report_type,
               i.status AS item_status, i.reported_date, i.contact AS item_contact,
               i.image_path AS item_image_path, i.description AS item_description,
               owner.full_name AS reporter_name,
               owner.student_id AS reporter_student_id
        FROM claims c
        LEFT JOIN users u ON u.user_id = c.student_id
        LEFT JOIN items i ON i.id = c.item_id
        LEFT JOIN users owner ON owner.user_id = i.user_id
        WHERE c.claim_id = ?
    """;

    try (Connection con = Database.connect();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, claimId);
        try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Claim details could not be found.");
                return;
            }

            String status = safe(rs.getString("claim_status"));
            String itemImage = rs.getString("item_image_path");
            String proofImage = rs.getString("proof_image_path");

            JDialog dialog = new JDialog(this, "Review claim #" + claimId, true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setSize(1000, 760);
            dialog.setMinimumSize(new Dimension(850, 650));
            dialog.setLocationRelativeTo(this);

            JPanel page = new JPanel(new BorderLayout(14, 14));
            page.setBackground(UITheme.PAGE);
            page.setBorder(BorderFactory.createEmptyBorder(18, 20, 16, 20));

            JPanel heading = new JPanel();
            heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
            heading.setOpaque(false);
            JLabel title = new JLabel("Claim review  #" + claimId);
            title.setFont(new Font("Segoe UI", Font.BOLD, 23));
            title.setForeground(UITheme.NAVY);
            JLabel subtitle = new JLabel("Check the student's details and visual evidence before making a decision.");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            subtitle.setForeground(UITheme.MUTED);
            JLabel statusLabel = new JLabel("STATUS: " + status.toUpperCase());
            statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
            statusLabel.setForeground("Pending".equalsIgnoreCase(status) ? new Color(164, 111, 33) : UITheme.BLUE_DARK);
            heading.add(title);
            heading.add(Box.createVerticalStrut(4));
            heading.add(subtitle);
            heading.add(Box.createVerticalStrut(8));
            heading.add(statusLabel);

            JPanel twoColumns = new JPanel(new GridLayout(1, 2, 14, 0));
            twoColumns.setOpaque(false);
            twoColumns.add(infoCard("STUDENT MAKING THE CLAIM", new String[][]{
                {"Name", safe(rs.getString("claimant_name"))},
                {"Student ID", safe(rs.getString("claimant_student_id"))},
                {"Email", safe(rs.getString("claimant_email"))},
                {"Phone", safe(rs.getString("claimant_phone"))},
                {"Submitted", safe(rs.getString("claim_date"))}
            }));
            twoColumns.add(infoCard("FOUND ITEM DETAILS", new String[][]{
                {"Item", safe(rs.getString("item_name")) + "  (#" + rs.getInt("item_id") + ")"},
                {"Category", safe(rs.getString("category"))},
                {"Location", safe(rs.getString("location"))},
                {"Report type", safe(rs.getString("report_type"))},
                {"Item status", safe(rs.getString("item_status"))},
                {"Reported", safe(rs.getString("reported_date"))},
                {"Contact on report", safe(rs.getString("item_contact"))},
                {"Reported by", safe(rs.getString("reporter_name"))},
                {"Item description", safe(rs.getString("item_description"))}
            }));

            JTextArea claimText = new JTextArea(safe(rs.getString("claim_description")));
            claimText.setLineWrap(true);
            claimText.setWrapStyleWord(true);
            claimText.setEditable(false);
            claimText.setFont(UITheme.FIELD);
            claimText.setForeground(UITheme.TEXT);
            claimText.setBackground(Color.WHITE);
            claimText.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            JScrollPane claimTextScroll = new JScrollPane(claimText);
            claimTextScroll.setPreferredSize(new Dimension(400, 105));
            claimTextScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
            JPanel descriptionCard = new JPanel(new BorderLayout(6, 8));
            descriptionCard.setBackground(Color.WHITE);
            descriptionCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
            JLabel descriptionTitle = new JLabel("CLAIM DESCRIPTION / OWNERSHIP DETAILS");
            descriptionTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
            descriptionTitle.setForeground(UITheme.MUTED);
            descriptionCard.add(descriptionTitle, BorderLayout.NORTH);
            descriptionCard.add(claimTextScroll, BorderLayout.CENTER);

            JPanel photos = new JPanel(new GridLayout(1, 2, 14, 0));
            photos.setOpaque(false);
            photos.add(photoCard("ITEM PHOTO", itemImage));
            photos.add(photoCard("STUDENT'S CLAIM PROOF PHOTO", proofImage));

            JPanel stack = new JPanel();
            stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
            stack.setOpaque(false);
            twoColumns.setAlignmentX(Component.LEFT_ALIGNMENT);
            descriptionCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            photos.setAlignmentX(Component.LEFT_ALIGNMENT);
            stack.add(twoColumns);
            stack.add(Box.createVerticalStrut(12));
            stack.add(descriptionCard);
            stack.add(Box.createVerticalStrut(12));
            stack.add(photos);

            JScrollPane scroll = new JScrollPane(stack);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.getVerticalScrollBar().setUnitIncrement(18);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            actions.setOpaque(false);
            JButton close = new JButton("Close");
            UITheme.styleSecondaryButton(close);
            actions.add(close);
            if ("Pending".equalsIgnoreCase(status)) {
                JButton approve = new JButton("Approve Claim");
                JButton reject = new JButton("Reject Claim");
                UITheme.stylePrimaryButton(approve);
                UITheme.stylePrimaryButton(reject);
                approve.setBackground(UITheme.SUCCESS);
                reject.setBackground(new Color(174, 70, 70));
                actions.add(approve, 0);
                actions.add(reject, 1);
                approve.addActionListener(e -> {
                    dialog.dispose();
                    updateClaim("Approved");
                });
                reject.addActionListener(e -> {
                    dialog.dispose();
                    updateClaim("Rejected");
                });
            }
            close.addActionListener(e -> dialog.dispose());

            page.add(heading, BorderLayout.NORTH);
            page.add(scroll, BorderLayout.CENTER);
            page.add(actions, BorderLayout.SOUTH);
            dialog.setContentPane(page);
            dialog.getContentPane().setBackground(UITheme.PAGE);
            dialog.setVisible(true);
        }
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Unable to open claim details: " + ex.getMessage());
    }
}

private JPanel infoCard(String title, String[][] values) {
    JPanel card = new JPanel();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(UITheme.BORDER),
        BorderFactory.createEmptyBorder(12, 14, 12, 14)));
    JLabel heading = new JLabel(title);
    heading.setFont(new Font("Segoe UI", Font.BOLD, 11));
    heading.setForeground(UITheme.MUTED);
    heading.setAlignmentX(Component.LEFT_ALIGNMENT);
    card.add(heading);
    card.add(Box.createVerticalStrut(8));
    for (String[] pair : values) {
        JLabel row = new JLabel("<html><b>" + escapeHtml(pair[0]) + ":</b> " + escapeHtml(pair[1]) + "</html>");
        row.setFont(UITheme.BODY);
        row.setForeground(UITheme.TEXT);
        row.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(row);
    }
    return card;
}

private JPanel photoCard(String title, String path) {
    JPanel card = new JPanel(new BorderLayout(6, 8));
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(UITheme.BORDER),
        BorderFactory.createEmptyBorder(12, 14, 12, 12)));
    JLabel heading = new JLabel(title);
    heading.setFont(new Font("Segoe UI", Font.BOLD, 11));
    heading.setForeground(UITheme.MUTED);
    JLabel preview = new JLabel("No photo attached", SwingConstants.CENTER);
    preview.setFont(UITheme.BODY);
    preview.setForeground(UITheme.MUTED);
    preview.setOpaque(true);
    preview.setBackground(new Color(247, 249, 252));
    preview.setPreferredSize(new Dimension(350, 210));
    preview.setBorder(BorderFactory.createLineBorder(new Color(235, 239, 244)));

    File imageFile = resolveImageFile(path);
    if (imageFile != null) {
        try {
            BufferedImage image = ImageIO.read(imageFile);
            if (image != null) {
                int maxW = 350, maxH = 210;
                double scale = Math.min((double) maxW / image.getWidth(), (double) maxH / image.getHeight());
                int width = Math.max(1, (int) Math.round(image.getWidth() * scale));
                int height = Math.max(1, (int) Math.round(image.getHeight() * scale));
                preview.setText("");
                preview.setIcon(new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH)));
                preview.setToolTipText(imageFile.getAbsolutePath());
            } else {
                preview.setText("File is not a supported image");
            }
        } catch (Exception ex) {
            preview.setText("Could not load photo");
        }
    } else if (path != null && !path.isBlank()) {
        preview.setText("Photo file not found");
    }
    card.add(heading, BorderLayout.NORTH);
    card.add(preview, BorderLayout.CENTER);
    return card;
}

private File resolveImageFile(String path) {
    if (path == null || path.isBlank()) return null;
    File direct = new File(path);
    if (direct.isFile()) return direct;
    String normalized = path.replace('\\', '/');
    String filename = normalized.substring(normalized.lastIndexOf('/') + 1);
    File[] candidates = {
        new File("images", filename),
        new File("images/claims", filename),
        new File("src/images", filename),
        new File("src/images/claims", filename)
    };
    for (File candidate : candidates) if (candidate.isFile()) return candidate;
    return null;
}

private String safe(String value) {
    return value == null || value.isBlank() ? "Not provided" : value;
}

private String escapeHtml(String value) {
    return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
}

// APPROVE / REJECT CLAIM
private void updateClaim(String newStatus) {

    int selectedRow = claimTable.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a claim first.");
        return;
    }

    int claimId = Integer.parseInt(
        claimModel.getValueAt(selectedRow, 0).toString()
    );

    int itemId = Integer.parseInt(
        claimModel.getValueAt(selectedRow, 1).toString()
    );

    String currentStatus = claimModel.getValueAt(selectedRow, 5).toString();

    if (!currentStatus.equalsIgnoreCase("Pending")) {
        JOptionPane.showMessageDialog(
            this, "This claim has already been processed."
        );
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to " + newStatus + " this claim?",
        "Confirm Action",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) return;

    String updateClaimSQL = """
        UPDATE claims
        SET status = ?
        WHERE claim_id = ? AND status = 'Pending'
    """;

    String updateItemSQL = """
        UPDATE items
        SET status = 'Returned'
        WHERE id = ? AND report_type = 'Found'
        AND status = 'Open'
    """;

    try (Connection con = Database.connect()) {

        con.setAutoCommit(false);

        try {

            try (PreparedStatement claimPS = con.prepareStatement(updateClaimSQL)) {
                claimPS.setString(1, newStatus);
                claimPS.setInt(2, claimId);

                if (claimPS.executeUpdate() == 0) {
                    con.rollback();
                    JOptionPane.showMessageDialog(this, "Claim could not be updated.");
                    return;
                }
            }

            if (newStatus.equals("Approved")) {
                try (PreparedStatement itemPS = con.prepareStatement(updateItemSQL)) {
                    itemPS.setInt(1, itemId);
                    itemPS.executeUpdate();
                }
            }

            if (newStatus.equals("Approved")) {
                try (PreparedStatement owner = con.prepareStatement("SELECT user_id FROM items WHERE id=?")) {
                    owner.setInt(1,itemId); try(ResultSet ownerRs=owner.executeQuery()) { if(ownerRs.next() && ownerRs.getObject(1)!=null) ProjectEnhancements.notifyUser(con,ownerRs.getInt(1),"Your found item has been claimed and marked returned."); }
                }
            }
            try (PreparedStatement who = con.prepareStatement("SELECT student_id FROM claims WHERE claim_id=?")) {
                who.setInt(1,claimId); try(ResultSet whoRs=who.executeQuery()) { if(whoRs.next()) ProjectEnhancements.notifyUser(con,whoRs.getInt(1),"Your claim for item #"+itemId+" was "+newStatus.toLowerCase()+"."); }
            }
            try (PreparedStatement hist = con.prepareStatement("INSERT INTO item_history(item_id,action,details) VALUES(?,?,?)")) {
                hist.setInt(1,itemId); hist.setString(2,"Claim "+newStatus); hist.setString(3,"Claim #"+claimId+" was "+newStatus.toLowerCase()); hist.executeUpdate();
            }
            con.commit();

            JOptionPane.showMessageDialog(
                this, "Claim " + newStatus + " successfully!"
            );

            loadAllData();

        } catch (Exception ex) {
            con.rollback();
            throw ex;
        }

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(
            this, "Error updating claim: " + ex.getMessage()
        );
    }
}


}
