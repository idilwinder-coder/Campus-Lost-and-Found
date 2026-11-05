import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Main extends JFrame {

    private final JTextField name = new JTextField();
    private final JTextField category = new JTextField();
    private final JTextField location = new JTextField();
    private final JTextField contact = new JTextField();
    private final JTextField search = new JTextField(15);
    private final JComboBox<String> dateFilter =
        new JComboBox<>(new String[]{
            "All Dates", "Today", "This Week", "This Month"
        });

    private final JComboBox<String> type =
            new JComboBox<>(new String[]{"Lost", "Found"});

    // The last column stores the image path and is hidden from the table.
    private final DefaultTableModel model = new DefaultTableModel(
          new String[]{"ID", "Item", "Category", "Location",
        "Type", "Status", "Date", "Contact", "Image"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);
    private final JLabel total = new JLabel("0");
    private final JLabel lost = new JLabel("0");
    private final JLabel found = new JLabel("0");
    private final JLabel returnedCount = new JLabel("0");
    private final JLabel imageLabel = new JLabel("No image selected");

    private String selectedImagePath = null;
    private boolean fillingFields = false;

    private final Color blue = new Color(37, 99, 235);
    private final Color dark = new Color(30, 41, 59);
    private final Color bg = new Color(245, 247, 251);

    public Main() {
        UITheme.apply();
        Database.createTable();

        setTitle("Campus Lost & Found Management System");
        setSize(1250, 750);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bg);
        setLayout(new BorderLayout(15, 15));

        JLabel title = new JLabel("  Campus Lost & Found");
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("  Manage lost and found items easily");
        subtitle.setForeground(new Color(219, 234, 254));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel heading = new JPanel(new GridLayout(2, 1));
        heading.setBackground(dark);
        heading.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        heading.add(title);
        heading.add(subtitle);
        add(heading, BorderLayout.NORTH);

        JPanel dashboard = new JPanel(new GridLayout(1, 4, 12, 12));
        dashboard.setBackground(bg);
        dashboard.add(card("TOTAL ITEMS", total, blue));
        dashboard.add(card("LOST ITEMS", lost, new Color(220, 70, 70)));
        dashboard.add(card("FOUND ITEMS", found, new Color(22, 163, 74)));
        dashboard.add(card("RETURNED", returnedCount, new Color(124, 58, 237)));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("  Report Item  "),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        addField(form, "Item Name", name);
        addField(form, "Category", category);
        addField(form, "Location", location);
        addField(form, "Contact Details", contact);
        addField(form, "Report Type", type);

        JButton addButton = button("+ Add Item", blue);
        JButton updateButton = button("Update Selected", new Color(14, 116, 144));
        JButton markButton = button("Mark Returned", new Color(22, 163, 74));
        JButton deleteButton = button("Delete Selected", new Color(220, 70, 70));
        JButton uploadButton = button("Upload Image", new Color(100, 116, 139));
        JButton viewButton = button("View Image", new Color(124, 58, 237));
        JButton clearButton = button("Clear Form", dark);

        JPanel imagePanel = new JPanel(new BorderLayout(5, 5));
        imagePanel.setBackground(Color.WHITE);
        imageLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        imagePanel.add(uploadButton, BorderLayout.WEST);
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        addField(form, "Item Image", imagePanel);

        JPanel actions = new JPanel(new GridLayout(0, 2, 8, 8));
        actions.setBackground(Color.WHITE);
        actions.add(addButton);
        actions.add(updateButton);
        actions.add(markButton);
        actions.add(deleteButton);
        actions.add(viewButton);
        actions.add(clearButton);

        JPanel formPanel = new JPanel(new BorderLayout(8, 12));
        formPanel.setBackground(bg);
        formPanel.add(form, BorderLayout.CENTER);
        formPanel.add(actions, BorderLayout.SOUTH);
        formPanel.setPreferredSize(new Dimension(365, 390));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchBar.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("Search item:");
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JButton findButton = button("Search", blue);
        JButton showAllButton = button("Show All", dark);

        searchBar.add(searchLabel);
        searchBar.add(search);
searchBar.add(findButton);
searchBar.add(showAllButton);
searchBar.add(new JLabel("Date:"));
searchBar.add(dateFilter);

        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(dark);
        table.setGridColor(new Color(226, 232, 240));
       table.setShowVerticalLines(true);
table.setShowHorizontalLines(true);
table.setIntercellSpacing(new Dimension(1, 1));
table.setRowHeight(75); 
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(226, 232, 240));
        table.getTableHeader().setForeground(dark);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));

        // Hide the image-path column; it remains available in the model.
       table.getColumnModel().getColumn(8).setCellRenderer(
    (tbl, value, isSelected, hasFocus, row, column) -> {

        JLabel label = new JLabel("", JLabel.CENTER);
        label.setOpaque(true);

        label.setBackground(isSelected
                ? tbl.getSelectionBackground()
                : Color.WHITE);

        if (value != null && !value.toString().isBlank()) {
            File file = new File(value.toString());

            if (file.exists()) {
                ImageIcon icon = new ImageIcon(file.getAbsolutePath());

                Image img = icon.getImage().getScaledInstance(
                        65, 65, Image.SCALE_SMOOTH);

                label.setIcon(new ImageIcon(img));
            } else {
                label.setText("Not Found");
            }
        } else {
            label.setText("No Image");
        }

        return label;
    }
); 

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel records = new JPanel(new BorderLayout(0, 10));
        records.setBackground(bg);
        records.add(searchBar, BorderLayout.NORTH);
        records.add(scroll, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(15, 15));
        center.setBackground(bg);
        center.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));
        center.add(dashboard, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(15, 0));
        content.setBackground(bg);
        content.add(formPanel, BorderLayout.WEST);
        content.add(records, BorderLayout.CENTER);
        center.add(content, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        addButton.addActionListener(e -> addItem());
        updateButton.addActionListener(e -> updateItem());
        markButton.addActionListener(e -> markReturned());
        deleteButton.addActionListener(e -> deleteItem());
        uploadButton.addActionListener(e -> chooseImage());
        viewButton.addActionListener(e -> viewImage());
        clearButton.addActionListener(e -> clearFields());
        findButton.addActionListener(e -> filterItems());
        search.addActionListener(e -> filterItems());
        showAllButton.addActionListener(e -> {
            search.setText("");
            loadData("");
        });
        dateFilter.addActionListener(e ->
        loadData(search.getText().trim()));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !fillingFields) {
                fillFieldsFromSelection();
            }
        });

        loadData("");
        UITheme.styleWindow(this);
        setVisible(true);
    }

    private JPanel card(String text, JLabel value, Color color) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, color),
                BorderFactory.createEmptyBorder(12, 15, 12, 10)));

        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(new Color(100, 116, 139));

        value.setFont(new Font("SansSerif", Font.BOLD, 27));
        value.setForeground(color);

        panel.add(label);
        panel.add(value);
        return panel;
    }

    private void addField(JPanel panel, String label, JComponent field) {
        JLabel text = new JLabel(label);
        text.setFont(new Font("SansSerif", Font.BOLD, 13));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        if (field instanceof JTextField) {
            field.setPreferredSize(new Dimension(150, 30));
        }
        panel.add(text);
        panel.add(field);
    }

    private JButton button(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void addItem() {
        if (!validateFields()) return;

        try {
            int id = Database.addItem(
                    name.getText().trim(),
                    category.getText().trim(),
                    location.getText().trim(),
                    type.getSelectedItem().toString(),
                    contact.getText().trim(),
                    selectedImagePath
            );

            if (id > 0) {
                JOptionPane.showMessageDialog(this, "Item added successfully. ID: " + id);
                clearFields();
                loadData("");
            } else {
                JOptionPane.showMessageDialog(this, "Item could not be added.");
            }
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void updateItem() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an item from the table first.");
            return;
        }
        if (!validateFields()) return;

        int modelRow = table.convertRowIndexToModel(row);
        int id = Integer.parseInt(model.getValueAt(modelRow, 0).toString());

        try {
            Database.updateItem(
                    id,
                    name.getText().trim(),
                    category.getText().trim(),
                    location.getText().trim(),
                    type.getSelectedItem().toString(),
                    contact.getText().trim(),
                    selectedImagePath
            );

            JOptionPane.showMessageDialog(this, "Item updated successfully.");
            clearFields();
            loadData("");
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private boolean validateFields() {
        if (name.getText().trim().isEmpty()
                || category.getText().trim().isEmpty()
                || location.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Enter item name, category and location.");
            return false;
        }
        return true;
    }

    private void fillFieldsFromSelection() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int modelRow = table.convertRowIndexToModel(row);
        fillingFields = true;

        name.setText(model.getValueAt(modelRow, 1).toString());
        category.setText(model.getValueAt(modelRow, 2).toString());
        location.setText(model.getValueAt(modelRow, 3).toString());
        type.setSelectedItem(model.getValueAt(modelRow, 4).toString());
        contact.setText(model.getValueAt(modelRow, 7) == null
                ? "" : model.getValueAt(modelRow, 7).toString());

        Object path = model.getValueAt(modelRow, 8);
        selectedImagePath = (path == null || path.toString().isBlank())
                ? null : path.toString();
        imageLabel.setText(selectedImagePath == null
                ? "No image saved" : new File(selectedImagePath).getName());

        fillingFields = false;
    }

    private void markReturned() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an item first.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int id = Integer.parseInt(model.getValueAt(modelRow, 0).toString());

        try {
            Database.markReturned(id);
            loadData(search.getText().trim());
            clearFields();
            JOptionPane.showMessageDialog(this, "Item marked as returned.");
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void deleteItem() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an item first.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int id = Integer.parseInt(model.getValueAt(modelRow, 0).toString());

        int choice = JOptionPane.showConfirmDialog(
                this, "Delete this item?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                Database.deleteItem(id);
                clearFields();
                loadData(search.getText().trim());
                JOptionPane.showMessageDialog(this, "Item deleted.");
            } catch (SQLException ex) {
                showDatabaseError(ex);
            }
        }
    }

private void filterItems() {
    String keyword = search.getText().trim();
    loadData(keyword);
}
    private void loadData(String keyword) {
        model.setRowCount(0);
String sql = "SELECT * FROM items WHERE 1=1";

if (keyword != null && !keyword.isBlank()) {
    sql += " AND (item_name LIKE ? OR category LIKE ? OR location LIKE ?)";
}

String selectedDate = dateFilter.getSelectedItem().toString();

switch (selectedDate) {
    case "Today":
        sql += " AND reported_date = CURDATE()";
        break;

    case "This Week":
        sql += " AND YEARWEEK(reported_date, 1) = YEARWEEK(CURDATE(), 1)";
        break;

    case "This Month":
        sql += " AND MONTH(reported_date) = MONTH(CURDATE())"
             + " AND YEAR(reported_date) = YEAR(CURDATE())";
        break;
}

sql += " ORDER BY id DESC";
        int totalItems = 0;
        int lostItems = 0;
        int foundItems = 0;
        int returnedItems = 0;

        try (Connection con = Database.connect();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword + "%";
                ps.setString(1, pattern);
                ps.setString(2, pattern);
                ps.setString(3, pattern);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String reportType = rs.getString("report_type");
                    String status = rs.getString("status");

                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("item_name"),
                            rs.getString("category"),
                            rs.getString("location"),
                            reportType,
                            status,
                            rs.getDate("reported_date"),
                            rs.getString("contact"),
                            rs.getString("image_path")
                    });

                    totalItems++;
                    if ("Lost".equalsIgnoreCase(reportType)) lostItems++;
                    if ("Found".equalsIgnoreCase(reportType)) foundItems++;
                    if ("Returned".equalsIgnoreCase(status)) returnedItems++;
                }
            }

            total.setText(String.valueOf(totalItems));
            lost.setText(String.valueOf(lostItems));
            found.setText(String.valueOf(foundItems));
            returnedCount.setText(String.valueOf(returnedItems));

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Item Image");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image files (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File source = chooser.getSelectedFile();

            try {
                File imageFolder = new File("images");
                if (!imageFolder.exists() && !imageFolder.mkdirs()) {
                    throw new IOException("Could not create images folder.");
                }

                String originalName = source.getName();
                String extension = "";
                int dot = originalName.lastIndexOf('.');
                if (dot >= 0) extension = originalName.substring(dot);

                File destination = new File(
                        imageFolder, UUID.randomUUID() + extension);

                Files.copy(source.toPath(), destination.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

                selectedImagePath = destination.getPath();
                imageLabel.setText(destination.getName());

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Image upload failed: " + ex.getMessage());
            }
        }
    }

    private void viewImage() {
        int row = table.getSelectedRow();
        String path = selectedImagePath;

        if (row != -1) {
            int modelRow = table.convertRowIndexToModel(row);
            Object value = model.getValueAt(modelRow, 8);
            path = value == null ? null : value.toString();
        }

        if (path == null || path.isBlank()) {
            JOptionPane.showMessageDialog(this, "No image available for this item.");
            return;
        }

        File file = new File(path);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Image file not found: " + path);
            return;
        }

        ImageIcon original = new ImageIcon(path);
        Image scaled = original.getImage().getScaledInstance(
                500, 400, Image.SCALE_SMOOTH);

        JLabel imageView = new JLabel(new ImageIcon(scaled));
        JOptionPane.showMessageDialog(this, imageView,
                "Item Image", JOptionPane.PLAIN_MESSAGE);
    }

    private void clearFields() {
        name.setText("");
        category.setText("");
        location.setText("");
        contact.setText("");
        type.setSelectedIndex(0);
        selectedImagePath = null;
        imageLabel.setText("No image selected");
        table.clearSelection();
    }

    private void showDatabaseError(SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "Database error: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
