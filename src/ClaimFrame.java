import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ClaimFrame extends JFrame {

    private final int userId;
    private JTextField itemIdField;
    private JTextArea descriptionArea;
    private JLabel photoNameLabel;
    private File selectedProofPhoto;

    public ClaimFrame(int userId) {
        UITheme.apply();
        this.userId = userId;

        setTitle("Submit a Found Item Claim");
        setSize(620, 590);
        setMinimumSize(new Dimension(560, 520));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UITheme.PAGE);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(UITheme.NAVY);
        header.setBorder(new EmptyBorder(22, 28, 20, 28));
        JLabel title = new JLabel("Claim a found item");
        title.setFont(new Font("Segoe UI", Font.BOLD, 23));
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("Share details that help the admin verify ownership.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(218, 226, 237));
        header.add(title);
        header.add(Box.createVerticalStrut(5));
        header.add(subtitle);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(22, 24, 22, 24)));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 7, 0);

        form.add(fieldLabel("FOUND ITEM ID"), g);
        g.gridy++;
        itemIdField = new JTextField();
        UITheme.styleInput(itemIdField);
        itemIdField.setPreferredSize(new Dimension(300, 40));
        form.add(itemIdField, g);

        g.gridy++; g.insets = new Insets(15, 0, 7, 0);
        form.add(fieldLabel("HOW CAN YOU IDENTIFY THIS ITEM?"), g);
        g.gridy++; g.insets = new Insets(0, 0, 7, 0);
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(UITheme.FIELD);
        descriptionArea.setForeground(UITheme.TEXT);
        descriptionArea.setBorder(new EmptyBorder(9, 10, 9, 10));
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        form.add(descriptionScroll, g);

        g.gridy++; g.insets = new Insets(15, 0, 6, 0);
        form.add(fieldLabel("PROOF PHOTO (OPTIONAL)"), g);
        g.gridy++; g.insets = new Insets(0, 0, 4, 0);
        JPanel photoRow = new JPanel(new BorderLayout(10, 0));
        photoRow.setOpaque(false);
        photoNameLabel = new JLabel("No photo selected");
        photoNameLabel.setForeground(UITheme.MUTED);
        JButton choosePhoto = new JButton("Choose photo");
        UITheme.styleSecondaryButton(choosePhoto);
        choosePhoto.addActionListener(e -> chooseProofPhoto());
        photoRow.add(photoNameLabel, BorderLayout.CENTER);
        photoRow.add(choosePhoto, BorderLayout.EAST);
        form.add(photoRow, g);

        g.gridy++; g.insets = new Insets(0, 0, 12, 0);
        JLabel hint = new JLabel("A clear photo, receipt, or other ownership evidence can help the admin review your claim.");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setForeground(UITheme.MUTED);
        form.add(hint, g);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton cancel = new JButton("Cancel");
        UITheme.styleSecondaryButton(cancel);
        JButton submit = new JButton("Submit for review");
        UITheme.stylePrimaryButton(submit);
        actions.add(cancel);
        actions.add(submit);
        g.gridy++; g.insets = new Insets(5, 0, 0, 0);
        g.anchor = GridBagConstraints.EAST;
        form.add(actions, g);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(UITheme.PAGE);
        body.setBorder(new EmptyBorder(20, 22, 22, 22));
        body.add(form, BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);
        root.add(body, BorderLayout.CENTER);
        setContentPane(root);

        choosePhoto.setToolTipText("Attach an optional image to help verify the claim");
        submit.addActionListener(e -> submitClaim());
        cancel.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(submit);

        UITheme.styleWindow(this);
        setVisible(true);
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(UITheme.MUTED);
        return label;
    }

    private void chooseProofPhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose claim proof photo");
        chooser.setFileFilter(new FileNameExtensionFilter(
            "Image files (JPG, JPEG, PNG, GIF)", "jpg", "jpeg", "png", "gif"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String name = file.getName().toLowerCase();
            if (!(name.endsWith(".jpg") || name.endsWith(".jpeg")
                    || name.endsWith(".png") || name.endsWith(".gif"))) {
                JOptionPane.showMessageDialog(this, "Please choose a JPG, PNG, or GIF image.");
                return;
            }
            if (file.length() > 8L * 1024L * 1024L) {
                JOptionPane.showMessageDialog(this, "Please choose an image smaller than 8 MB.");
                return;
            }
            selectedProofPhoto = file;
            photoNameLabel.setText(file.getName());
            photoNameLabel.setForeground(UITheme.TEXT);
        }
    }

    private String saveProofPhoto() throws Exception {
        if (selectedProofPhoto == null) return null;
        File folder = new File("images/claims");
        if (!folder.exists() && !folder.mkdirs()) {
            throw new java.io.IOException("Could not create images/claims folder.");
        }
        String original = selectedProofPhoto.getName().replaceAll("[^a-zA-Z0-9._-]", "_");
        String safeName = "claim_" + System.currentTimeMillis() + "_" + original;
        File destination = new File(folder, safeName);
        Files.copy(selectedProofPhoto.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return "images/claims/" + safeName;
    }

    private void submitClaim() {
        String itemText = itemIdField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (itemText.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter the item ID and ownership details.");
            return;
        }
        if (description.length() < 10) {
            JOptionPane.showMessageDialog(this, "Please provide at least 10 characters of ownership details.");
            return;
        }

        String savedPath = null;
        try {
            int itemId = Integer.parseInt(itemText);
            savedPath = saveProofPhoto();
            boolean success = Database.submitClaim(itemId, userId, description, savedPath);

            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Claim submitted. The admin will review your details and any attached photo.");
                dispose();
            } else {
                if (savedPath != null) Files.deleteIfExists(new File(savedPath).toPath());
                JOptionPane.showMessageDialog(this,
                    "Claim not submitted. Check that the item is Found and Open, belongs to another student, and has no existing claim from you.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric item ID.");
        } catch (Exception ex) {
            if (savedPath != null) {
                try { Files.deleteIfExists(new File(savedPath).toPath()); } catch (Exception ignored) { }
            }
            JOptionPane.showMessageDialog(this, "Could not submit claim: " + ex.getMessage());
        }
    }
}
