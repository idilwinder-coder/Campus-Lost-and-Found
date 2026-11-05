import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MyClaimsFrame extends JFrame {

    private int userId;
    private JTable table;
    private DefaultTableModel model;

    public MyClaimsFrame(int userId) {

        this.userId = userId;

        setTitle("My Claims - Campus Lost & Found");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        main.setBackground(Color.WHITE);

        JLabel heading = new JLabel("My Submitted Claims");
        heading.setFont(new Font("Arial", Font.BOLD, 25));
        heading.setForeground(new Color(35, 91, 190));

        String[] columns = {
            "Claim ID",
            "Item ID",
            "Item Name",
            "Category",
            "Description",
            "Status",
            "Claim Date"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(table);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadClaims());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(refreshButton);

        main.add(heading, BorderLayout.NORTH);
        main.add(scrollPane, BorderLayout.CENTER);
        main.add(bottom, BorderLayout.SOUTH);

        add(main);

        loadClaims();

        setVisible(true);
    }

    private void loadClaims() {

        model.setRowCount(0);

        String sql = """
            SELECT c.claim_id,
                   c.item_id,
                   i.item_name,
                   i.category,
                   c.claim_description,
                   c.status,
                   c.claim_date
            FROM claims c
            JOIN items i ON c.item_id = i.id
            WHERE c.student_id = ?
            ORDER BY c.claim_id DESC
        """;

        try (
            Connection con = Database.connect();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    model.addRow(new Object[] {
                        rs.getInt("claim_id"),
                        rs.getInt("item_id"),
                        rs.getString("item_name"),
                        rs.getString("category"),
                        rs.getString("claim_description"),
                        rs.getString("status"),
                        rs.getTimestamp("claim_date")
                    });
                }
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                    this, "You have not submitted any claims yet."
                );
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                this, "Error loading claims: " + ex.getMessage()
            );
        }
    }
}