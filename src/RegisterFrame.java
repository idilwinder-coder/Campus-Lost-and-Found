import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private JTextField nameField;
    private JTextField studentIdField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public RegisterFrame() {
        UITheme.apply();
        setTitle("Create Student Account | Campus Lost & Found");
        setSize(700, 650);
        setMinimumSize(new Dimension(620, 590));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(UITheme.PAGE);
        root.setBorder(new EmptyBorder(22, 24, 22, 24));

        JPanel card = new JPanel(new BorderLayout(0, 18));
        card.setBackground(UITheme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(27, 32, 27, 32)));

        JPanel heading = new JPanel();
        heading.setBackground(UITheme.CARD);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Create student account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(UITheme.NAVY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitle = new JLabel("Enter your campus details to get started.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(UITheme.MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        heading.add(title);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitle);

        nameField = new JTextField();
        studentIdField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(UITheme.CARD);
        addRow(fields, 0, "Full name", nameField);
        addRow(fields, 1, "Student ID", studentIdField);
        addRow(fields, 2, "University email", emailField);
        addRow(fields, 3, "Phone number", phoneField);
        addRow(fields, 4, "Password", passwordField);
        addRow(fields, 5, "Confirm password", confirmPasswordField);

        JPanel footer = new JPanel(new BorderLayout(12, 0));
        footer.setBackground(UITheme.CARD);
        JLabel accountLabel = new JLabel("STUDENT ACCOUNT");
        accountLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        accountLabel.setForeground(UITheme.MUTED);

        JButton registerButton = new JButton("Create account");
        UITheme.stylePrimaryButton(registerButton);
        JButton clearButton = new JButton("Clear");
        UITheme.styleSecondaryButton(clearButton);
        JButton loginButton = new JButton("Back to sign in");
        UITheme.styleLinkButton(loginButton);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(UITheme.CARD);
        actions.add(loginButton);
        actions.add(clearButton);
        actions.add(registerButton);

        footer.add(accountLabel, BorderLayout.WEST);
        footer.add(actions, BorderLayout.EAST);

        card.add(heading, BorderLayout.NORTH);
        card.add(fields, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        root.add(card, gbc);
        setContentPane(root);

        registerButton.addActionListener(e -> registerStudent());
        clearButton.addActionListener(e -> clearFields());
        loginButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        UITheme.styleWindow(this);
        setVisible(true);
    }

    private void addRow(JPanel panel, int row, String labelText, JTextField field) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.weightx = 0;
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(7, 0, 7, 18);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(UITheme.TEXT);
        label.setPreferredSize(new Dimension(135, 40));
        panel.add(label, labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row;
        fieldConstraints.weightx = 1;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.insets = new Insets(7, 0, 7, 0);
        UITheme.styleInput(field);
        panel.add(field, fieldConstraints);
    }

    private void registerStudent() {
        String name = nameField.getText().trim();
        String studentId = studentIdField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (name.isEmpty() || studentId.isEmpty() || email.isEmpty() || phone.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            JOptionPane.showMessageDialog(this, "Enter a valid email address!");
            return;
        }
        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password must contain at least 8 characters!");
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!");
            return;
        }

        try {
            boolean registered = Database.registerUser(name, studentId, email, phone, password);
            if (registered) {
                String[] user = Database.authenticateUser(email, password);
                if (user != null) {
                    int userId = Integer.parseInt(user[0]);
                    String fullName = user[1];
                    JOptionPane.showMessageDialog(this, "Registration successful!");
                    dispose();
                    new StudentDashboard(userId, fullName);
                } else {
                    JOptionPane.showMessageDialog(this, "Registration successful! Please sign in.");
                    dispose();
                    new LoginFrame();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Student ID or email already exists!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage());
        }
    }

    private void clearFields() {
        nameField.setText("");
        studentIdField.setText("");
        emailField.setText("");
        phoneField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

    public static void main(String[] args) {
        UITheme.apply();
        SwingUtilities.invokeLater(RegisterFrame::new);
    }
}
