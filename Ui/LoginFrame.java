package Ui;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;
import models.User;
import storages.DataStore;

public class LoginFrame extends JFrame {
    private JTextField nameField, contactField;
    private JComboBox<String> roleBox;
    private JButton loginBtn, registerBtn;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");

    public LoginFrame() {
        setTitle("AidBridge Login / Registration");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 2, 5, 5));

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Contact:"));
        contactField = new JTextField();
        add(contactField);

        add(new JLabel("Role:"));
        roleBox = new JComboBox<>(new String[]{"Admin", "Volunteer", "Victim"});
        add(roleBox);

        loginBtn = new JButton("Login");
        registerBtn = new JButton("Register");
        add(loginBtn);
        add(registerBtn);

        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> register());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void login() {
        String name = nameField.getText().trim();
        String contact = normalizeContact(contactField.getText());
        String role = (String) roleBox.getSelectedItem();

        // Validate contact first
        if (!isValidContact(contact)) {
            JOptionPane.showMessageDialog(this, "Enter a valid 10-digit contact number.", "Invalid Contact", JOptionPane.ERROR_MESSAGE);
            contactField.requestFocus();
            return;
        }

        List<User> users = DataStore.loadUsers();
        for (User u : users) {
            // Login requires matching both contact and role
            if (u.getContact().equals(contact) && u.getRole().equals(role)) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                new DashboardFrame(u);
                dispose();
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "User not found! Please register first.", "Login Failed", JOptionPane.WARNING_MESSAGE);
    }

    private void register() {
        String name = nameField.getText().trim();
        String contactRaw = contactField.getText();
        String contact = normalizeContact(contactRaw);
        String role = (String) roleBox.getSelectedItem();

        // Basic validations
        if (name.isEmpty() || name.length() < 2) {
            JOptionPane.showMessageDialog(this, "Enter a valid name (at least 2 characters).", "Invalid Name", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return;
        }

        if (!isValidContact(contact)) {
            JOptionPane.showMessageDialog(this, "Enter a valid 10-digit contact number.", "Invalid Contact", JOptionPane.ERROR_MESSAGE);
            contactField.requestFocus();
            return;
        }

        if (role == null || role.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a role.", "Invalid Role", JOptionPane.ERROR_MESSAGE);
            roleBox.requestFocus();
            return;
        }
        List<User> users = DataStore.loadUsers();
        for (User u : users) {
            // Only prevent duplication if both contact and role are same
            if (u.getContact().equals(contact) && u.getRole().equals(role)) {
                JOptionPane.showMessageDialog(this, "Contact already registered for this role. Try logging in.", "Duplicate Contact", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        int newId = 1;
        for (User u : users) {
            if (u.getId() >= newId) newId = u.getId() + 1;
        }

        users.add(new User(newId, name, role, contact));
        DataStore.saveUsers(users);

        JOptionPane.showMessageDialog(this, "Registration Successful! You can now log in.");
    }
    private String normalizeContact(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("\\D", ""); // remove everything except digits
    }

    private boolean isValidContact(String contact) {
        return contact != null && PHONE_PATTERN.matcher(contact).matches();
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}
