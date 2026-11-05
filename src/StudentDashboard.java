import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.sql.*;

public class StudentDashboard extends JFrame {

    private int userId;
    private String studentName;

    private final Color blue = new Color(35, 91, 190);
    private final Color background = new Color(244, 247, 252);
    private final Color sidebarColor = new Color(24, 53, 105);

    private JPanel recentItemsPanel;

    public StudentDashboard(int userId, String studentName) {
        UITheme.apply();

        this.userId = userId;
        this.studentName = studentName;

        setTitle("Campus Lost & Found - Student Dashboard");
        setSize(1150, 720);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(background);

        // SIDEBAR
        JPanel sidebar = new JPanel();
        sidebar.setBackground(sidebarColor);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 18, 25, 18));

        JLabel logo = new JLabel("CAMPUS");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 27));

        JLabel logo2 = new JLabel("LOST & FOUND");
        logo2.setForeground(new Color(180, 205, 255));
        logo2.setFont(new Font("Arial", Font.BOLD, 14));

        sidebar.add(logo);
        sidebar.add(logo2);
        sidebar.add(Box.createVerticalStrut(45));

        addMenu(sidebar, "Dashboard");
        addMenu(sidebar, "My Profile");
        addMenu(sidebar, "My Reports");
        addMenu(sidebar, "Report Lost Item");
        addMenu(sidebar, "Report Found Item");
        addMenu(sidebar, "Search Items");
        addMenu(sidebar, "Claim Found Item");
        addMenu(sidebar, "My Claims");
        JButton extraTools = new JButton("Notifications & Filters");
        extraTools.setMaximumSize(new Dimension(195, 40));
        extraTools.addActionListener(e -> {
            Object[] options = {"Notifications", "Advanced Search", "Item History", "Close"};
            int pick = JOptionPane.showOptionDialog(this, "Choose an option", "More tools", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (pick == 0) ProjectEnhancements.openNotifications(this, userId);
            else if (pick == 1) ProjectEnhancements.openAdvancedSearch(this);
            else if (pick == 2) ProjectEnhancements.openHistory(this);
        });
        sidebar.add(extraTools);

        sidebar.add(Box.createVerticalGlue());

        JButton logout = new JButton("Logout");
        logout.setMaximumSize(new Dimension(190, 42));
        logout.setFont(new Font("Arial", Font.BOLD, 15));
        logout.setFocusPainted(false);

        logout.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        sidebar.add(logout);

        // MAIN CONTENT
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBackground(background);
        content.setBorder(new EmptyBorder(25, 25, 25, 25));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel welcome = new JLabel("Welcome, " + studentName + "!");
        welcome.setFont(new Font("Arial", Font.BOLD, 29));
        welcome.setForeground(new Color(30, 45, 75));

        JLabel subtitle = new JLabel(
            "Manage your lost and found items from one place."
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 15));
        subtitle.setForeground(Color.GRAY);

        JPanel heading = new JPanel(new GridLayout(2, 1, 5, 5));
        heading.setOpaque(false);
        heading.add(welcome);
        heading.add(subtitle);

        header.add(heading, BorderLayout.WEST);

        // PROFILE CARD
        JPanel profileCard = new JPanel(new BorderLayout(15, 15));
        profileCard.setBackground(Color.WHITE);

        profileCard.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 231, 240)),
                new EmptyBorder(20, 20, 20, 20)
            )
        );

        JLabel profileTitle = new JLabel("STUDENT PROFILE");
        profileTitle.setFont(new Font("Arial", Font.BOLD, 17));
        profileTitle.setForeground(blue);

        JPanel details = new JPanel(new GridLayout(2, 3, 20, 15));
        details.setBackground(Color.WHITE);

        try {

            String[] user = Database.getUserDetails(userId);

            if (user != null) {
                details.add(detail("Full Name", user[1]));
                details.add(detail("Student ID", user[2]));
                details.add(detail("University Email", user[3]));
                details.add(detail("Phone Number", user[4]));
                details.add(detail("User ID", user[0]));
                details.add(detail("Account Type", "STUDENT"));
            } else {
                details.add(new JLabel("Student details not found."));
            }

        } catch (Exception ex) {
            details.add(new JLabel("Unable to load profile."));
            ex.printStackTrace();
        }

        profileCard.add(profileTitle, BorderLayout.NORTH);
        profileCard.add(details, BorderLayout.CENTER);

        // FEATURE CARDS
        JPanel features = new JPanel(new GridLayout(2, 2, 15, 15));
        features.setOpaque(false);

        features.add(createCard(
            "Report Lost Item",
            "Report something you have lost.",
            "REPORT LOST"
        ));

        features.add(createCard(
            "Report Found Item",
            "Help another student find their item.",
            "REPORT FOUND"
        ));

        features.add(createCard(
            "Search Items",
            "Search items reported on campus.",
            "SEARCH ITEMS"
        ));

        features.add(createCard(
            "Claim Found Item",
            "Submit a claim for an item you lost.",
            "SUBMIT CLAIM"
        ));

        // RECENT ITEMS SECTION
        JPanel recentSection = new JPanel(new BorderLayout(10, 15));
        recentSection.setOpaque(false);

        JLabel recentTitle = new JLabel("Recent Lost & Found Items");
        recentTitle.setFont(new Font("Arial", Font.BOLD, 22));
        recentTitle.setForeground(new Color(30, 55, 100));

        recentItemsPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        recentItemsPanel.setOpaque(false);

        recentSection.add(recentTitle, BorderLayout.NORTH);
        recentSection.add(recentItemsPanel, BorderLayout.CENTER);

        // CENTER CONTENT
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(background);

        profileCard.setPreferredSize(new Dimension(850, 170));
        profileCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));
        profileCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        features.setPreferredSize(new Dimension(850, 250));
        features.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        features.setAlignmentX(Component.LEFT_ALIGNMENT);

        recentSection.setPreferredSize(new Dimension(850, 280));
        recentSection.setMinimumSize(new Dimension(850, 280));
        recentSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        recentSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        center.add(profileCard);
        center.add(Box.createVerticalStrut(20));
        center.add(features);
        center.add(Box.createVerticalStrut(25));
        center.add(recentSection);

        // SCROLL PANE
        JScrollPane contentScroll = new JScrollPane(center);

        contentScroll.setBorder(null);
        contentScroll.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );
        contentScroll.getVerticalScrollBar().setUnitIncrement(20);
        contentScroll.setBackground(background);

        content.add(header, BorderLayout.NORTH);
        content.add(contentScroll, BorderLayout.CENTER);

        main.add(sidebar, BorderLayout.WEST);
        main.add(content, BorderLayout.CENTER);

        add(main);

        loadRecentItems();

        UITheme.styleWindow(this);
        setVisible(true);
    }

    // LOAD RECENT ITEMS
    private void loadRecentItems() {

        recentItemsPanel.removeAll();

        String sql = """
            SELECT id, item_name, report_type, location, image_path
            FROM items
            WHERE status = 'Open'
            ORDER BY id DESC
            LIMIT 3
        """;

        try (
            Connection con = Database.connect();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            int count = 0;

            while (rs.next()) {

                int id = rs.getInt("id");
                String name = rs.getString("item_name");
                String type = rs.getString("report_type");
                String location = rs.getString("location");
                String imagePath = rs.getString("image_path");

                recentItemsPanel.add(
                    createRecentCard(id, name, type, location, imagePath)
                );

                count++;
            }

            if (count == 0) {
                JLabel empty = new JLabel("No recent items available.");
                empty.setFont(new Font("Arial", Font.PLAIN, 15));
                recentItemsPanel.add(empty);
            }

        } catch (Exception ex) {

            JLabel error = new JLabel("Unable to load recent items.");
            recentItemsPanel.add(error);

            ex.printStackTrace();
        }

        recentItemsPanel.revalidate();
        recentItemsPanel.repaint();
    }

    // CREATE RECENT ITEM CARD
    private JPanel createRecentCard(
        int id,
        String name,
        String type,
        String location,
        String imagePath
    ) {

        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(Color.WHITE);

        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 231, 240)),
                new EmptyBorder(12, 12, 12, 12)
            )
        );

        // IMAGE
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(180, 115));

        File file = imagePath == null ? null : new File(imagePath);

        if (file != null && file.exists()) {

            ImageIcon icon = new ImageIcon(imagePath);

            Image scaled = icon.getImage().getScaledInstance(
                180, 115, Image.SCALE_SMOOTH
            );

            imageLabel.setIcon(new ImageIcon(scaled));

        } else {

            imageLabel.setText("No Image Available");
            imageLabel.setForeground(Color.GRAY);
        }

        // ITEM INFORMATION
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel typeLabel = new JLabel("Type: " + type);
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        typeLabel.setForeground(blue);

        JLabel locationLabel = new JLabel("Location: " + location);
        locationLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel info = new JPanel(new GridLayout(3, 1, 4, 4));
        info.setOpaque(false);

        info.add(nameLabel);
        info.add(typeLabel);
        info.add(locationLabel);

        // VIEW IMAGE BUTTON
        JButton viewButton = new JButton("View Image");

        viewButton.setBackground(blue);
        viewButton.setForeground(Color.WHITE);
        viewButton.setFocusPainted(false);

        viewButton.addActionListener(e -> showItemImage(imagePath));

        card.add(imageLabel, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        card.add(viewButton, BorderLayout.SOUTH);

        return card;
    }

    // SHOW LARGE IMAGE
    private void showItemImage(String imagePath) {

        if (imagePath == null || imagePath.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No image uploaded.");
            return;
        }

        File file = new File(imagePath);

        if (!file.exists()) {
            JOptionPane.showMessageDialog(
                this, "Image file not found:\n" + imagePath
            );
            return;
        }

        ImageIcon icon = new ImageIcon(imagePath);

        Image scaled = icon.getImage().getScaledInstance(
            500, 400, Image.SCALE_SMOOTH
        );

        JLabel label = new JLabel(new ImageIcon(scaled));

        JOptionPane.showMessageDialog(
            this,
            label,
            "Item Image Preview",
            JOptionPane.PLAIN_MESSAGE
        );
    }

    // SIDEBAR MENU
    private void addMenu(JPanel sidebar, String text) {

        JButton button = new JButton(text);

        button.setMaximumSize(new Dimension(195, 43));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setForeground(Color.WHITE);
        button.setBackground(sidebarColor);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);

        button.addActionListener(e -> {

            switch (text) {

                case "Dashboard":
                    JOptionPane.showMessageDialog(
                        this, "You are already on Dashboard."
                    );
                    break;

                case "My Profile":
                    JOptionPane.showMessageDialog(
                        this, "Your profile is displayed on Dashboard."
                    );
                    break;

                case "My Reports":
                    new MyReportsFrame(userId);
                    break;

                case "Report Lost Item":
                    new ItemReportFrame(userId, "Lost");
                    break;

                case "Report Found Item":
                    new ItemReportFrame(userId, "Found");
                    break;

                case "Search Items":
                    new SearchItemsFrame(userId);
                    break;

                case "Claim Found Item":
                    new ClaimFrame(userId);
                    break;

                case "My Claims":
                    new MyClaimsFrame(userId);
                    break;
            }
        });

        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(8));
    }

    // PROFILE DETAIL
    private JPanel detail(String title, String value) {

        JPanel panel = new JPanel(new GridLayout(2, 1, 3, 5));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(new Color(35, 45, 65));
        valueLabel.setFont(new Font("Arial", Font.BOLD, 15));

        panel.add(titleLabel);
        panel.add(valueLabel);

        return panel;
    }

    // FEATURE CARD
    private JPanel createCard(
        String title,
        String description,
        String buttonText
    ) {

        JPanel card = new JPanel(new BorderLayout(10, 12));

        card.setBackground(Color.WHITE);
        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 231, 240)),
                new EmptyBorder(20, 20, 20, 20)
            )
        );

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 19));
        titleLabel.setForeground(new Color(30, 55, 100));

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        descriptionLabel.setForeground(Color.GRAY);

        JButton action = new JButton(buttonText);

        action.setBackground(blue);
        action.setForeground(Color.WHITE);
        action.setFont(new Font("Arial", Font.BOLD, 13));
        action.setFocusPainted(false);

        action.addActionListener(e -> {

            switch (title) {

                case "Report Lost Item":
                    new ItemReportFrame(userId, "Lost");
                    break;

                case "Report Found Item":
                    new ItemReportFrame(userId, "Found");
                    break;

                case "Search Items":
                    new SearchItemsFrame(userId);
                    break;

                case "Claim Found Item":
                    new ClaimFrame(userId);
                    break;
            }
        });

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        textPanel.setOpaque(false);

        textPanel.add(titleLabel);
        textPanel.add(descriptionLabel);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(action, BorderLayout.SOUTH);

        return card;
    }
}