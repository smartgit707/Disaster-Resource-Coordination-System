package Ui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import models.Request;
import models.Resource;
import models.User;
import storages.DataStore;
import util.F2SmsSender;

public class AdminRequestsFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public AdminRequestsFrame() {
        setTitle("Admin - Manage Requests");
        setSize(900, 450);
        setLayout(new BorderLayout());

        String[] cols = {"ID", "VictimId", "Type", "Location", "Urgency", "Status", "AssignedVolunteer", "AllocatedResource"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        JButton allocateBtn = new JButton("Allocate Resource");
        JButton sendAlertBtn = new JButton("Send Alert");
        btnPanel.add(refreshBtn);
        btnPanel.add(allocateBtn);
        btnPanel.add(sendAlertBtn);
        add(btnPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadRequests());
        allocateBtn.addActionListener(e -> allocateSelected());
        sendAlertBtn.addActionListener(e -> sendAlertToVictimAndVolunteer());

        loadRequests();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadRequests() {
        model.setRowCount(0);
        List<Request> requests = DataStore.loadRequests();
        for (Request r : requests) {
            if ("Escalated".equals(r.getStatus()) || "Pending".equals(r.getStatus()) || "Assigned".equals(r.getStatus())) {
                model.addRow(new Object[]{
                    r.getId(), r.getUserId(), r.getType(), r.getLocation(), r.getUrgency(),
                    r.getStatus(), r.getAssignedVolunteerId(), r.getAllocatedResourceId()
                });
            }
        }
    }

    private void allocateSelected() {
        int sel = table.getSelectedRow();
        if (sel == -1) { JOptionPane.showMessageDialog(this, "Select a request first"); return; }
        int reqId = (int) model.getValueAt(sel, 0);
        Request r = DataStore.getRequestById(reqId);
        if (r == null) { JOptionPane.showMessageDialog(this, "Request not found"); return; }

        List<Resource> resources = DataStore.loadResources();
        if (resources.isEmpty()) { JOptionPane.showMessageDialog(this, "No resources available"); return; }

        String[] options = new String[resources.size()];
        for (int i = 0; i < resources.size(); i++) {
            Resource res = resources.get(i);
            options[i] = res.getId() + " - " + res.getType() + " (qty: " + res.getQuantity() + ")";
        }

        String selRes = (String) JOptionPane.showInputDialog(this, "Select resource to allocate:", "Allocate Resource",
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (selRes == null) return;

        int resId = Integer.parseInt(selRes.split(" - ")[0]);
        Resource chosen = DataStore.getResourceById(resId);
        if (chosen == null || chosen.getQuantity() <= 0) {
            JOptionPane.showMessageDialog(this, "Selected resource not available.");
            return;
        }

        String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity to allocate (available: " + chosen.getQuantity() + "):", "1");
        if (qtyStr == null) return;
        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity.");
            return;
        }
        if (qty <= 0 || qty > chosen.getQuantity()) {
            JOptionPane.showMessageDialog(this, "Quantity out of range.");
            return;
        }

        chosen.setQuantity(chosen.getQuantity() - qty);
        DataStore.updateResource(chosen);

        r.allocateResource(chosen.getId());
        r.setStatus("Allocated");
        r.setAdminNotes("Allocated " + qty + " of resource " + chosen.getType());
        DataStore.updateRequest(r);

        JOptionPane.showMessageDialog(this, "Resource allocated and request updated.");
        loadRequests();
    }

    private void sendAlertToVictimAndVolunteer() {
        int sel = table.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Select a request first!");
            return;
        }
        int reqId = (int) model.getValueAt(sel, 0);
        Request r = DataStore.getRequestById(reqId);
        if (r == null) {
            JOptionPane.showMessageDialog(this, "Request not found!");
            return;
        }

        String victimPhoneNumber = getPhoneNumberFromVictimId(r.getUserId());

        // Null check and get volunteer phone number only if volunteer assigned
        String volunteerPhoneNumber = null;
        Integer volunteerId = r.getAssignedVolunteerId();
        if (volunteerId != null) {
            volunteerPhoneNumber = getPhoneNumberFromVictimId(volunteerId);
        }

        if ((victimPhoneNumber == null || victimPhoneNumber.isEmpty()) && (volunteerPhoneNumber == null || volunteerPhoneNumber.isEmpty())) {
            JOptionPane.showMessageDialog(this, "Neither victim's nor volunteer's phone number found!");
            return;
        }

        if (victimPhoneNumber != null && victimPhoneNumber.startsWith("+91")) {
            victimPhoneNumber = victimPhoneNumber.substring(3);
        }
        if (volunteerPhoneNumber != null && volunteerPhoneNumber.startsWith("+91")) {
            volunteerPhoneNumber = volunteerPhoneNumber.substring(3);
        }

        String defaultMessage = "Alert: Your request (ID: " + r.getId() + ") status is '" + r.getStatus() + "'. Please contact your assigned volunteer or admin.";

        String alertMsg = JOptionPane.showInputDialog(this, "Enter alert message:", defaultMessage);
        if (alertMsg == null || alertMsg.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Alert message cannot be empty.");
            return;
        }

        int sentCount = 0;
        try {
            if (victimPhoneNumber != null && !victimPhoneNumber.isEmpty()) {
                F2SmsSender.sendSms(alertMsg, victimPhoneNumber);
                sentCount++;
            }
            if (volunteerPhoneNumber != null && !volunteerPhoneNumber.isEmpty()) {
                F2SmsSender.sendSms(alertMsg, volunteerPhoneNumber);
                sentCount++;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to send SMS: " + ex.getMessage());
            return;
        }
        JOptionPane.showMessageDialog(this, "Alert SMS sent successfully to " + sentCount + " recipient(s)!");
    }


    private String getPhoneNumberFromVictimId(int victimId) {
        User user = DataStore.getUserById(victimId);
        return user != null ? user.getContact() : null;
    }
}
