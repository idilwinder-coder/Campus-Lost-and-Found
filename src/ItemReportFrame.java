import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ItemReportFrame extends JFrame {

    private int userId;
    private String reportType;

    private JTextField nameField;
    private JComboBox<String> categoryField;
    private JTextField locationField;
    private JTextField contactField;
    private JTextField imageField;

    private JTextArea descriptionArea;

    public ItemReportFrame(int userId, String reportType) {
        UITheme.apply();

        this.userId = userId;
        this.reportType = reportType;

        setTitle("Report " + reportType + " Item");
        setSize(550, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 2, 12, 12));
        panel.setBorder(
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        );

        panel.add(new JLabel("Item Name:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Category:"));
        categoryField = new JComboBox<>(new String[]{"Electronics", "ID Cards", "Bags", "Books", "Clothing", "Keys", "Accessories", "Other"});
        categoryField.setEditable(true);
        panel.add(categoryField);

        panel.add(new JLabel("Location:"));
        locationField = new JTextField();
        panel.add(locationField);

        panel.add(new JLabel("Contact Number:"));
        contactField = new JTextField();
        panel.add(contactField);

        panel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(4, 20);
        panel.add(new JScrollPane(descriptionArea));

        panel.add(new JLabel("Item Image:"));

        JPanel imagePanel = new JPanel(new BorderLayout(5, 5));
        imageField = new JTextField();
        imageField.setEditable(false);

        JButton browseButton = new JButton("Browse");

        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();

            if (chooser.showOpenDialog(this)
                    == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                imageField.setText(file.getAbsolutePath());
            }
        });

        imagePanel.add(imageField, BorderLayout.CENTER);
        imagePanel.add(browseButton, BorderLayout.EAST);

        panel.add(imagePanel);

        JButton submitButton = new JButton("Submit Report");
        JButton cancelButton = new JButton("Cancel");

        panel.add(submitButton);
        panel.add(cancelButton);

        submitButton.addActionListener(e -> submitReport());

        cancelButton.addActionListener(e -> dispose());

        add(panel);
        UITheme.styleWindow(this);
        setVisible(true);
    }

    private void submitReport() {

        String name = nameField.getText().trim();
        String category = categoryField.getSelectedItem().toString().trim();
        String location = locationField.getText().trim();
        String contact = contactField.getText().trim();
        String description = descriptionArea.getText().trim();
        String imagePath = imageField.getText().trim();

        if (name.isEmpty() || category.isEmpty()
                || location.isEmpty() || contact.isEmpty()) {

            JOptionPane.showMessageDialog(
                this, "Please fill all required fields!"
            );
            return;
        }

        try {

            if (!imagePath.isEmpty()) {
                File source = new File(imagePath);
                if (source.exists()) {
                    File folder = new File("images"); folder.mkdirs();
                    String safeName = System.currentTimeMillis() + "_" + source.getName().replaceAll("[^a-zA-Z0-9._-]", "_");
                    File target = new File(folder, safeName);
                    java.nio.file.Files.copy(source.toPath(), target.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    imagePath = target.getPath();
                }
            }
            int id = Database.addStudentItem(
                userId,
                name,
                category,
                description,
                location,
                reportType,
                contact,
                imagePath
            );

            if (id > 0) {
                try (java.sql.Connection c = Database.connect(); java.sql.PreparedStatement h = c.prepareStatement("INSERT INTO item_history(item_id,changed_by,action,details) VALUES(?,?,?,?)")) {
                    h.setInt(1,id); h.setInt(2,userId); h.setString(3,"Item reported"); h.setString(4,reportType + " item submitted by student"); h.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Report submitted successfully! ID: " + id);
                dispose();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Error: " + ex.getMessage()
            );
        }
    }
}