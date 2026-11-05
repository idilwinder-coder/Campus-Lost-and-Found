import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.io.File;

public class SearchItemsFrame extends JFrame {

private JTextField searchField;
private JComboBox<String> typeBox;
private JTable table;
private DefaultTableModel model;
private int userId;

public SearchItemsFrame(int userId) {

    this.userId = userId;

    setTitle("Search Campus Lost & Found Items");
    setSize(1100, 650);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    JPanel main = new JPanel(new BorderLayout(15, 15));
    main.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
    main.setBackground(new Color(244, 247, 252));

    JLabel heading = new JLabel("Search Lost & Found Items");
    heading.setFont(new Font("Arial", Font.BOLD, 26));

    JPanel top = new JPanel(new BorderLayout());
    top.setOpaque(false);
    top.add(heading, BorderLayout.NORTH);

    JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
    filters.setOpaque(false);

    searchField = new JTextField(22);
    typeBox = new JComboBox<>(new String[]{"All", "Lost", "Found"});

    JButton searchButton = new JButton("Search");
    JButton refreshButton = new JButton("Refresh");

    filters.add(new JLabel("Search:"));
    filters.add(searchField);
    filters.add(new JLabel("Type:"));
    filters.add(typeBox);
    filters.add(searchButton);
    filters.add(refreshButton);

    top.add(filters, BorderLayout.CENTER);

    model = new DefaultTableModel(
        new String[]{
            "ID", "Item Name", "Category", "Location",
            "Type", "Status", "Date", "Description", "Image Path"
        }, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    table = new JTable(model);
    table.setRowHeight(30);
    table.setFont(new Font("Arial", Font.PLAIN, 13));
    table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

    // Hide image path column from normal table view
    table.removeColumn(table.getColumnModel().getColumn(8));

    JScrollPane scrollPane = new JScrollPane(table);

    JButton detailsButton = new JButton("View Selected Item");
    JButton imageButton = new JButton("View Image");

    detailsButton.addActionListener(e -> showDetails());
    imageButton.addActionListener(e -> showImage());

    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    bottom.setOpaque(false);
    bottom.add(detailsButton);
    bottom.add(imageButton);

    searchButton.addActionListener(e -> loadItems());

    refreshButton.addActionListener(e -> {
        searchField.setText("");
        typeBox.setSelectedIndex(0);
        loadItems();
    });

    searchField.addActionListener(e -> loadItems());

    main.add(top, BorderLayout.NORTH);
    main.add(scrollPane, BorderLayout.CENTER);
    main.add(bottom, BorderLayout.SOUTH);

    add(main);

    loadItems();
    setVisible(true);
}

private void loadItems() {

    model.setRowCount(0);

    String keyword = searchField.getText().trim();
    String type = typeBox.getSelectedItem().toString();

    String sql = """
        SELECT id, item_name, category, location,
               report_type, status, reported_date,
               description, image_path
        FROM items
        WHERE status = 'Open'
        AND (
            item_name LIKE ?
            OR category LIKE ?
            OR location LIKE ?
        )
        """;

    if (!type.equals("All")) {
        sql += " AND report_type = ?";
    }

    sql += " ORDER BY id DESC";

    try (Connection con = Database.connect();
         PreparedStatement ps = con.prepareStatement(sql)) {

        String search = "%" + keyword + "%";

        ps.setString(1, search);
        ps.setString(2, search);
        ps.setString(3, search);

        if (!type.equals("All")) {
            ps.setString(4, type);
        }

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("item_name"),
                    rs.getString("category"),
                    rs.getString("location"),
                    rs.getString("report_type"),
                    rs.getString("status"),
                    rs.getDate("reported_date"),
                    rs.getString("description"),
                    rs.getString("image_path")
                });
            }
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No matching items found!");
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
    }
}

private void showDetails() {

    int row = table.getSelectedRow();

    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Please select an item first!");
        return;
    }

    JOptionPane.showMessageDialog(
        this,
        "Item ID: " + model.getValueAt(row, 0)
        + "\nName: " + model.getValueAt(row, 1)
        + "\nCategory: " + model.getValueAt(row, 2)
        + "\nLocation: " + model.getValueAt(row, 3)
        + "\nType: " + model.getValueAt(row, 4)
        + "\nStatus: " + model.getValueAt(row, 5)
        + "\nDate: " + model.getValueAt(row, 6)
        + "\nDescription: " + model.getValueAt(row, 7)
    );
}

private void showImage() {

    int row = table.getSelectedRow();

    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Please select an item first!");
        return;
    }

    String path = (String) model.getValueAt(row, 8);

    if (path == null || path.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "No image uploaded for this item.");
        return;
    }

    File file = new File(path);

    if (!file.exists()) {
        JOptionPane.showMessageDialog(this, "Image file not found:\n" + path);
        return;
    }

    ImageIcon original = new ImageIcon(path);

    Image scaled = original.getImage().getScaledInstance(
        450, 350, Image.SCALE_SMOOTH
    );

    JLabel imageLabel = new JLabel(new ImageIcon(scaled));

    JOptionPane.showMessageDialog(
        this,
        imageLabel,
        "Item Image Preview",
        JOptionPane.PLAIN_MESSAGE
    );
}


}
