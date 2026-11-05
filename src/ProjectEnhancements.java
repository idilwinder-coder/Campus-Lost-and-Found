import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.YearMonth;

/** Small, database-backed additions for the Campus Lost & Found project. */
public class ProjectEnhancements {
    private ProjectEnhancements() {}

    public static void migrate() {
        try (Connection c = Database.connect(); Statement s = c.createStatement()) {
            addColumn(s, "items", "user_id", "INT NULL");
            addColumn(s, "items", "description", "TEXT NULL");
            addColumn(s, "claims", "proof_details", "TEXT NULL");
            addColumn(s, "claims", "proof_image_path", "VARCHAR(500) NULL");
            addColumn(s, "claims", "reviewed_at", "TIMESTAMP NULL");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS notifications (notification_id INT AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, message VARCHAR(500) NOT NULL, is_read TINYINT(1) NOT NULL DEFAULT 0, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, INDEX(user_id)) ENGINE=InnoDB");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS item_history (history_id INT AUTO_INCREMENT PRIMARY KEY, item_id INT NOT NULL, changed_by INT NULL, action VARCHAR(80) NOT NULL, details VARCHAR(500), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, INDEX(item_id)) ENGINE=InnoDB");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS password_resets (reset_id INT AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, reset_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, INDEX(user_id)) ENGINE=InnoDB");
        } catch (SQLException e) {
            System.err.println("Feature migration could not finish: " + e.getMessage());
        }
    }
    private static void addColumn(Statement s, String table, String column, String type) throws SQLException {
        try { s.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + column + " " + type); }
        catch (SQLException e) { if (!e.getMessage().toLowerCase().contains("duplicate column")) throw e; }
    }
    public static boolean resetPassword(String email, String studentId, String phone, String newPassword) throws Exception {
        String sql = "SELECT user_id FROM users WHERE email=? AND student_id=? AND phone=?";
        try (Connection c=Database.connect(); PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1,email); p.setString(2,studentId); p.setString(3,phone);
            try(ResultSet r=p.executeQuery()) { if(!r.next()) return false; int id=r.getInt(1);
                // Reuse the registration hashing format through the helper below.
                String hash=hash(newPassword);
                try(PreparedStatement u=c.prepareStatement("UPDATE users SET password_hash=? WHERE user_id=?")) { u.setString(1,hash); u.setInt(2,id); u.executeUpdate(); }
                try(PreparedStatement h=c.prepareStatement("INSERT INTO password_resets(user_id) VALUES(?)")) { h.setInt(1,id); h.executeUpdate(); }
                notifyUser(c,id,"Your account password was changed."); return true;
            }
        }
    }
    private static String hash(String password) throws Exception {
        byte[] salt=new byte[16]; new java.security.SecureRandom().nextBytes(salt);
        javax.crypto.spec.PBEKeySpec spec=new javax.crypto.spec.PBEKeySpec(password.toCharArray(),salt,65536,256);
        byte[] value=javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); spec.clearPassword();
        return java.util.Base64.getEncoder().encodeToString(salt)+":"+java.util.Base64.getEncoder().encodeToString(value);
    }
    public static void notifyUser(Connection c,int userId,String message) throws SQLException {
        try(PreparedStatement p=c.prepareStatement("INSERT INTO notifications(user_id,message) VALUES(?,?)")){p.setInt(1,userId);p.setString(2,message);p.executeUpdate();}
    }
    public static void openNotifications(Component parent,int userId) {
        showQuery(parent,"My Notifications","SELECT message AS Notification, created_at AS Date, IF(is_read=1,'Read','New') AS Status FROM notifications WHERE user_id="+userId+" ORDER BY notification_id DESC");
        try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("UPDATE notifications SET is_read=1 WHERE user_id=?")){p.setInt(1,userId);p.executeUpdate();}catch(Exception ignored){}
    }
    public static void openHistory(Component parent) {
        showQuery(parent,"Item History","SELECT h.history_id AS ID, i.item_name AS Item, h.action AS Action, h.details AS Details, h.created_at AS Date FROM item_history h LEFT JOIN items i ON i.id=h.item_id ORDER BY h.history_id DESC");
    }
    public static void openAnalytics(Component parent) {
        String sql="SELECT DATE_FORMAT(reported_date,'%Y-%m') AS Month, COUNT(*) AS Reports, SUM(report_type='Lost') AS Lost, SUM(report_type='Found') AS Found, SUM(status='Returned') AS Returned FROM items GROUP BY DATE_FORMAT(reported_date,'%Y-%m') ORDER BY Month";
        DefaultTableModel model=new DefaultTableModel(new String[]{"Month","Reports","Lost","Found","Returned"},0);
        java.util.List<String> labels=new java.util.ArrayList<>(); java.util.List<Integer> values=new java.util.ArrayList<>();
        try(Connection c=Database.connect();Statement st=c.createStatement();ResultSet rs=st.executeQuery(sql)){
            while(rs.next()){String month=rs.getString("Month");int count=rs.getInt("Reports");labels.add(month);values.add(count);model.addRow(new Object[]{month,count,rs.getInt("Lost"),rs.getInt("Found"),rs.getInt("Returned")});}
        }catch(Exception e){JOptionPane.showMessageDialog(parent,"Analytics unavailable: "+e.getMessage());return;}
        JTable table=new JTable(model);table.setRowHeight(24);
        JPanel chart=new JPanel(){ protected void paintComponent(Graphics g){super.paintComponent(g); if(values.isEmpty()){g.drawString("No report data yet",20,30);return;} int max=1;for(int v:values)max=Math.max(max,v);int w=Math.max(1,getWidth()/values.size()); for(int i=0;i<values.size();i++){int h=(int)((getHeight()-55)*(values.get(i)/(double)max));int x=i*w+18;int y=getHeight()-35-h;g.setColor(new Color(45,105,205));g.fillRoundRect(x,y,Math.max(12,w-34),h,8,8);g.setColor(new Color(35,45,60));g.drawString(labels.get(i),x-4,getHeight()-15);g.drawString(String.valueOf(values.get(i)),x,y-5);}}};
        chart.setPreferredSize(new Dimension(700,220));chart.setBackground(Color.WHITE);
        JPanel body=new JPanel(new BorderLayout(8,8));body.add(new JLabel("Monthly reported items"),BorderLayout.NORTH);body.add(chart,BorderLayout.CENTER);body.add(new JScrollPane(table),BorderLayout.SOUTH);
        body.setPreferredSize(new Dimension(760,440));JOptionPane.showMessageDialog(parent,body,"Monthly Item Analytics",JOptionPane.PLAIN_MESSAGE);
    }
    public static void openAdvancedSearch(Component parent) {
        JTextField keyword=new JTextField(16); JComboBox<String> type=new JComboBox<>(new String[]{"All","Lost","Found"});
        JComboBox<String> category=new JComboBox<>(new String[]{"All","Electronics","ID Cards","Bags","Books","Clothing","Keys","Accessories","Other"});
        JTextField location=new JTextField(12); JTextField fromDate=new JTextField(10); JTextField toDate=new JTextField(10);
        JPanel filters=new JPanel(new FlowLayout(FlowLayout.LEFT)); filters.add(new JLabel("Keyword"));filters.add(keyword);filters.add(new JLabel("Type"));filters.add(type);filters.add(new JLabel("Category"));filters.add(category);filters.add(new JLabel("Location"));filters.add(location);filters.add(new JLabel("From (YYYY-MM-DD)"));filters.add(fromDate);filters.add(new JLabel("To"));filters.add(toDate);
        int choice=JOptionPane.showConfirmDialog(parent,filters,"Advanced item filters",JOptionPane.OK_CANCEL_OPTION);
        if(choice!=JOptionPane.OK_OPTION)return;
        StringBuilder sql=new StringBuilder("SELECT id AS ID,item_name AS Item,category AS Category,location AS Location,report_type AS Type,status AS Status,reported_date AS Date FROM items WHERE status='Open'");
        String k=keyword.getText().trim().replace("'","''"), cat=category.getSelectedItem().toString(), loc=location.getText().trim().replace("'","''");
        String from=fromDate.getText().trim(), to=toDate.getText().trim();
        if(!k.isEmpty())sql.append(" AND (item_name LIKE '%").append(k).append("%' OR description LIKE '%").append(k).append("%' OR location LIKE '%").append(k).append("%')");
        if(!"All".equals(type.getSelectedItem()))sql.append(" AND report_type='").append(type.getSelectedItem()).append("'");
        if(!"All".equals(cat))sql.append(" AND category='").append(cat).append("'");
        if(!loc.isEmpty())sql.append(" AND location LIKE '%").append(loc).append("%'");
        if(!from.isEmpty())sql.append(" AND reported_date >= '").append(from.replace("'","''")).append("'");
        if(!to.isEmpty())sql.append(" AND reported_date <= '").append(to.replace("'","''")).append("'");
        sql.append(" ORDER BY id DESC"); showQuery(parent,"Filtered Items",sql.toString());
    }
    public static void showQuery(Component parent,String title,String sql) {
        DefaultTableModel model=new DefaultTableModel(); JTable table=new JTable(model); table.setRowHeight(25);
        try(Connection c=Database.connect();Statement s=c.createStatement();ResultSet r=s.executeQuery(sql)){
            ResultSetMetaData m=r.getMetaData(); for(int i=1;i<=m.getColumnCount();i++)model.addColumn(m.getColumnLabel(i));
            while(r.next()){Object[] row=new Object[m.getColumnCount()];for(int i=0;i<row.length;i++)row[i]=r.getObject(i+1);model.addRow(row);}
        }catch(Exception e){JOptionPane.showMessageDialog(parent,"Could not load data: "+e.getMessage());return;}
        JScrollPane pane=new JScrollPane(table);pane.setPreferredSize(new Dimension(850,400));JOptionPane.showMessageDialog(parent,pane,title,JOptionPane.PLAIN_MESSAGE);
    }
}
