import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    public LoginFrame() {
        UITheme.apply();
        ProjectEnhancements.migrate();

        setTitle("Campus Lost & Found | Sign in");
        setSize(900, 620);
        setMinimumSize(new Dimension(800, 570));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new GridLayout(1, 2));
        root.setBackground(UITheme.PAGE);

        // Left panel: simple campus identity and a short explanation.
        JPanel brand = new JPanel();
        brand.setBackground(UITheme.NAVY);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(new EmptyBorder(48, 46, 38, 42));

        JLabel brandMark = new JLabel("CAMPUS");
        brandMark.setForeground(Color.WHITE);
        brandMark.setFont(new Font("Segoe UI", Font.BOLD, 32));
        JLabel brandSub = new JLabel("LOST  &  FOUND");
        brandSub.setForeground(new Color(190, 205, 226));
        brandSub.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel headline = new JLabel("Lost something?");
        headline.setForeground(Color.WHITE);
        headline.setFont(new Font("Segoe UI", Font.BOLD, 26));
        JLabel copy = new JLabel("<html>Report it here, check what has been found,<br>and help belongings get back to their owners.</html>");
        copy.setForeground(new Color(218, 226, 237));
        copy.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel note = new JLabel("Made for our campus community.");
        note.setForeground(new Color(190, 205, 226));
        note.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        brand.add(brandMark);
        brand.add(Box.createVerticalStrut(3));
        brand.add(brandSub);
        brand.add(Box.createVerticalGlue());
        brand.add(headline);
        brand.add(Box.createVerticalStrut(13));
        brand.add(copy);
        brand.add(Box.createVerticalGlue());
        brand.add(note);

        // Right panel: a content-sized card. No fixed-height clipping on Windows scaling.
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(UITheme.PAGE);
        right.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel card = new JPanel();
        card.setBackground(UITheme.CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(27, 30, 24, 30)));

        JLabel title = new JLabel("Welcome back");
        title.setFont(new Font("Segoe UI", Font.BOLD, 25));
        title.setForeground(UITheme.NAVY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in with your campus account");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(UITheme.MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = fieldLabel("SIGN IN AS");
        roleBox = new JComboBox<>(new String[]{"STUDENT", "ADMIN"});
        roleBox.setFont(UITheme.FIELD);
        roleBox.setBackground(Color.WHITE);
        roleBox.setForeground(UITheme.TEXT);
        roleBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        roleBox.setPreferredSize(new Dimension(300, 42));
        roleBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel emailLabel = fieldLabel("UNIVERSITY EMAIL");
        emailField = new JTextField();
        UITheme.styleInput(emailField);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passwordLabel = fieldLabel("PASSWORD");
        passwordField = new JPasswordField();
        UITheme.styleInput(passwordField);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginButton = new JButton("Sign in");
        UITheme.stylePrimaryButton(loginButton);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginButton.setPreferredSize(new Dimension(300, 44));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton registerButton = new JButton("Create student account");
        UITheme.styleSecondaryButton(registerButton);
        registerButton.setBorder(BorderFactory.createEmptyBorder(7, 2, 7, 2));
        registerButton.setOpaque(false);
        registerButton.setContentAreaFilled(false);

        JButton forgotButton = new JButton("Forgot password?");
        UITheme.styleLinkButton(forgotButton);

        JPanel links = new JPanel(new BorderLayout(12, 0));
        links.setBackground(Color.WHITE);
        links.setAlignmentX(Component.LEFT_ALIGNMENT);
        links.add(registerButton, BorderLayout.WEST);
        links.add(forgotButton, BorderLayout.EAST);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(21));
        card.add(roleLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(roleBox);
        card.add(Box.createVerticalStrut(14));
        card.add(emailLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(emailField);
        card.add(Box.createVerticalStrut(14));
        card.add(passwordLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(20));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(9));
        card.add(links);

        Dimension cardPreferred = card.getPreferredSize();
        card.setPreferredSize(new Dimension(390, cardPreferred.height));
        card.setMaximumSize(new Dimension(440, cardPreferred.height));
        right.add(card);

        root.add(brand);
        root.add(right);
        setContentPane(root);

        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> {
            dispose();
            new RegisterFrame();
        });
        forgotButton.addActionListener(e -> forgotPassword());
        getRootPane().setDefaultButton(loginButton);

        UITheme.styleWindow(this);
        setVisible(true);
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(UITheme.MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void forgotPassword() {
        JTextField email = new JTextField(20);
        JTextField studentId = new JTextField(20);
        JTextField phone = new JTextField(20);
        JPasswordField next = new JPasswordField(20);
        UITheme.styleInput(email);
        UITheme.styleInput(studentId);
        UITheme.styleInput(phone);
        UITheme.styleInput(next);

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.setBorder(new EmptyBorder(8, 4, 4, 4));
        form.add(new JLabel("University email"));
        form.add(email);
        form.add(new JLabel("Student / Staff ID"));
        form.add(studentId);
        form.add(new JLabel("Registered phone number"));
        form.add(phone);
        form.add(new JLabel("New password"));
        form.add(next);

        int result = JOptionPane.showConfirmDialog(this, form, "Reset password", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String password = new String(next.getPassword());
        if (email.getText().trim().isEmpty() || studentId.getText().trim().isEmpty()
                || phone.getText().trim().isEmpty() || password.length() < 8) {
            JOptionPane.showMessageDialog(this,
                "Enter all details. Password must contain at least 8 characters.");
            return;
        }
        try {
            boolean changed = ProjectEnhancements.resetPassword(
                email.getText().trim(), studentId.getText().trim(), phone.getText().trim(), password);
            JOptionPane.showMessageDialog(this,
                changed ? "Password updated. Please sign in." : "The account details did not match.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Reset failed: " + ex.getMessage());
        }
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String selectedRole = roleBox.getSelectedItem().toString();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password!");
            return;
        }
        try {
            String[] user = Database.authenticateUser(email, password);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Invalid email or password!");
                return;
            }
            int userId = Integer.parseInt(user[0]);
            String name = user[1];
            String actualRole = user[2];
            if (!selectedRole.equalsIgnoreCase(actualRole)) {
                JOptionPane.showMessageDialog(this,
                    "You are not registered as " + selectedRole + "!");
                return;
            }
            JOptionPane.showMessageDialog(this, "Welcome " + name + "!");
            dispose();
            if ("ADMIN".equalsIgnoreCase(actualRole)) new AdminDashboard();
            else new StudentDashboard(userId, name);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Login Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        UITheme.apply();
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
