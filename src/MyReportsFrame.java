import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MyReportsFrame extends JFrame {

private int userId;
private DefaultTableModel model;
private JTable table;

public MyReportsFrame(int userId) {

    this.userId = userId;

    setTitle("My Reports - Campus Lost & Found");
    setSize(1000, 600);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    JPanel main = new JPanel(new BorderLayout(15, 15));
    main.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
    main.setBackground(new Color(244, 247, 252));

    JLabel heading = new JLabel("My Lost & Found Reports");
    heading.setFont(new Font("Arial", Font.BOLD, 26));

    model = new DefaultTableModel(
        new String[]{
            "ID", "Item Name", "Category", "Location",
            "Type", "Status", "Reported Date"
        }, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    table = new JTable(model);
    table.setRowHeight(30);

    JButton refresh = new JButton("Refresh");
    refresh.addActionListener(e -> loadReports());

    JPanel top = new JPanel(new BorderLayout());
    top.setOpaque(false);
    top.add(heading, BorderLayout.WEST);
    top.add(refresh, BorderLayout.EAST);

    main.add(top, BorderLayout.NORTH);
    main.add(new JScrollPane(table), BorderLayout.CENTER);

    add(main);

    loadReports();

    setVisible(true);
}

private void loadReports() {

    model.setRowCount(0);

    try (Connection con = Database.connect();
         ResultSet rs = Database.getMyReports(con, userId)) {

        while (rs.next()) {

            model.addRow(new Object[]{
                rs.getInt("id"),
                rs.getString("item_name"),
                rs.getString("category"),
                rs.getString("location"),
                rs.getString("report_type"),
                rs.getString("status"),
                rs.getDate("reported_date")
            });
        }

    } catch (SQLException ex) {

        JOptionPane.showMessageDialog(
            this, "Error: " + ex.getMessage()
        );
    }
}

}
