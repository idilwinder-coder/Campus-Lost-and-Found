import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.border.Border;
import java.awt.*;

/** Shared, understated visual theme for the Campus Lost & Found Swing application. */
public final class UITheme {
    public static final Color NAVY = new Color(27, 43, 65);
    public static final Color NAVY_LIGHT = new Color(39, 59, 85);
    public static final Color BLUE = new Color(48, 103, 184);
    public static final Color BLUE_DARK = new Color(37, 82, 148);
    public static final Color PAGE = new Color(245, 247, 250);
    public static final Color CARD = Color.WHITE;
    public static final Color TEXT = new Color(42, 52, 66);
    public static final Color MUTED = new Color(111, 122, 137);
    public static final Color BORDER = new Color(222, 228, 235);
    public static final Color SUCCESS = new Color(49, 128, 99);
    public static final Font BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FIELD = new Font("Segoe UI", Font.PLAIN, 14);

    private static boolean applied;

    private UITheme() { }

    public static void apply() {
        if (applied) return;
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("control", PAGE);
            UIManager.put("info", CARD);
            UIManager.put("nimbusBase", NAVY);
            UIManager.put("nimbusBlueGrey", new Color(224, 230, 238));
            UIManager.put("nimbusFocus", BLUE);
            UIManager.put("nimbusSelectionBackground", new Color(218, 231, 248));
            UIManager.put("nimbusSelectedText", TEXT);
            UIManager.put("text", TEXT);
            UIManager.put("controlText", TEXT);
            UIManager.put("defaultFont", BODY);
            UIManager.put("Button.font", BODY_BOLD);
            UIManager.put("Label.font", BODY);
            UIManager.put("TextField.font", FIELD);
            UIManager.put("TextField.background", CARD);
            UIManager.put("TextField.foreground", TEXT);
            UIManager.put("PasswordField.background", CARD);
            UIManager.put("PasswordField.foreground", TEXT);
            UIManager.put("ComboBox.font", FIELD);
            UIManager.put("ComboBox.background", CARD);
            UIManager.put("ComboBox.foreground", TEXT);
            UIManager.put("TextArea.font", FIELD);
            UIManager.put("TabbedPane.font", BODY_BOLD);
            UIManager.put("Table.font", BODY);
            UIManager.put("TableHeader.font", BODY_BOLD);
            UIManager.put("Panel.background", PAGE);
            UIManager.put("OptionPane.messageFont", BODY);
            UIManager.put("OptionPane.buttonFont", BODY_BOLD);
            applied = true;
        } catch (Exception ex) {
            System.err.println("Could not apply application theme: " + ex.getMessage());
        }
    }

    public static void styleWindow(JFrame frame) {
        frame.getContentPane().setBackground(PAGE);
        styleTree(frame.getContentPane());
    }

    public static void stylePrimaryButton(JButton button) {
        button.setFont(BODY_BOLD);
        button.setBackground(BLUE);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
    }

    public static void styleSecondaryButton(JButton button) {
        button.setFont(BODY_BOLD);
        button.setBackground(CARD);
        button.setForeground(BLUE_DARK);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(9, 15, 9, 15)));
    }

    public static void styleLinkButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(BLUE_DARK);
        button.setBackground(CARD);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(6, 2, 6, 2));
    }

    public static void styleInput(JTextField field) {
        field.setFont(FIELD);
        field.setForeground(TEXT);
        field.setBackground(CARD);
        field.setCaretColor(BLUE);
        field.setPreferredSize(new Dimension(220, 42));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(8, 11, 8, 11)));
    }

    private static void styleTree(Component component) {
        if (component instanceof JTable table) {
            table.setFont(BODY);
            table.setRowHeight(32);
            table.setGridColor(BORDER);
            table.setShowVerticalLines(false);
            table.setSelectionBackground(new Color(225, 236, 250));
            table.setSelectionForeground(TEXT);
            table.setFillsViewportHeight(true);
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                header.setFont(BODY_BOLD);
                header.setBackground(new Color(235, 240, 246));
                header.setForeground(NAVY);
                header.setPreferredSize(new Dimension(header.getWidth(), 38));
            }
        } else if (component instanceof JPasswordField field) {
            styleInput(field);
        } else if (component instanceof JTextField field) {
            styleInput(field);
        } else if (component instanceof JTextArea area) {
            area.setFont(FIELD);
            area.setForeground(TEXT);
            area.setBackground(CARD);
        } else if (component instanceof JComboBox<?> combo) {
            combo.setFont(FIELD);
            combo.setForeground(TEXT);
            combo.setBackground(CARD);
            combo.setBorder(BorderFactory.createLineBorder(BORDER));
        } else if (component instanceof JButton button) {
            button.setFont(BODY_BOLD);
            button.setFocusPainted(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else if (component instanceof JLabel label) {
            if (label.getFont().getFamily().equalsIgnoreCase("Dialog")) {
                label.setFont(BODY);
            }
            label.setForeground(label.getForeground() == null ? TEXT : label.getForeground());
        }

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) styleTree(child);
        }
    }
}
