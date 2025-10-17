package Ui;
import javax.swing.*; 
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import models.Resource;
import models.User;
import storages.DataStore;

public class ResourceFrame extends JFrame {
    private User user;
    private JTable table;
    private DefaultTableModel model;

    public ResourceFrame(User user) {
        this.user = user;
        setTitle("Resources - AidBridge");
        setSize(600, 400);
        setLayout(new BorderLayout());

        String[] columns = {"ID", "Type", "Quantity", "Location"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel panel = new JPanel();

        if (user.getRole().equals("Admin")) {
            JButton addBtn = new JButton("Add Resource");
            JButton updateBtn = new JButton("Update Resource");
            panel.add(addBtn);
            panel.add(updateBtn);

            addBtn.addActionListener(e -> addResource());
            updateBtn.addActionListener(e -> updateResource());
        }

        JButton refreshBtn = new JButton("Refresh");
        panel.add(refreshBtn);
        refreshBtn.addActionListener(e -> loadResources());

        add(panel, BorderLayout.SOUTH);

        loadResources();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadResources() {
        model.setRowCount(0);
        List<Resource> resources = DataStore.loadResources();
        for (Resource r : resources) {
            model.addRow(new Object[]{r.getId(), r.getType(), r.getQuantity(), r.getLocation()});
        }
    }

    private void addResource() {
        JTextField typeField = new JTextField();
        JTextField qtyField = new JTextField();
        JTextField locField = new JTextField();
        Object[] fields = {"Type:", typeField, "Quantity:", qtyField, "Location:", locField};
        int option = JOptionPane.showConfirmDialog(this, fields, "Add Resource", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String type = typeField.getText();
            int qty = Integer.parseInt(qtyField.getText());
            String loc = locField.getText();

            List<Resource> resources = DataStore.loadResources();
            int id = resources.size() + 1;
            resources.add(new Resource(id, type, qty, loc));
            DataStore.saveResources(resources);
            loadResources();
        }
    }

    private void updateResource() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Select a resource first!");
            return;
        }

        List<Resource> resources = DataStore.loadResources();
        Resource r = resources.get(selected);

        JTextField typeField = new JTextField(r.getType());
        JTextField qtyField = new JTextField(String.valueOf(r.getQuantity()));
        JTextField locField = new JTextField(r.getLocation());
        Object[] fields = {"Type:", typeField, "Quantity:", qtyField, "Location:", locField};
        int option = JOptionPane.showConfirmDialog(this, fields, "Update Resource", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            r.setType(typeField.getText());
            r.setQuantity(Integer.parseInt(qtyField.getText()));
            r.setLocation(locField.getText());
            DataStore.saveResources(resources);
            loadResources();
        }
    }
}
