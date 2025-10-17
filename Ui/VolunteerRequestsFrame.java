package Ui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import models.Request;
import models.User;
import storages.DataStore;

public class VolunteerRequestsFrame extends JFrame {
    private User volunteer;
    private JTable table;
    private DefaultTableModel model;

    public VolunteerRequestsFrame(User volunteer) {
        this.volunteer = volunteer;
        setTitle("Volunteer - Pending Requests");
        setSize(800, 400);
        setLayout(new BorderLayout());

        String[] cols = {"ID", "VictimId", "Type", "Location", "Urgency", "Status", "AssignedVolunteer"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        JButton acceptBtn = new JButton("Accept (Assign to me)");
        JButton escalateBtn = new JButton("Escalate to Admin");
        JButton completeBtn = new JButton("Mark Completed");
        btnPanel.add(refreshBtn);
        btnPanel.add(acceptBtn);
        btnPanel.add(escalateBtn);
        btnPanel.add(completeBtn);
        add(btnPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadRequests());
        acceptBtn.addActionListener(e -> acceptSelected());
        escalateBtn.addActionListener(e -> escalateSelected());
        completeBtn.addActionListener(e -> completeSelected());

        loadRequests();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadRequests() {
        model.setRowCount(0);
        List<Request> requests = DataStore.loadRequests();
        for (Request r : requests) {
            if ("Pending".equals(r.getStatus()) || "Escalated".equals(r.getStatus()) 
                || "Assigned".equals(r.getStatus()) || "Allocated".equals(r.getStatus())) {
                model.addRow(new Object[]{
                    r.getId(), r.getUserId(), r.getType(), r.getLocation(), r.getUrgency(),
                    r.getStatus(), r.getAssignedVolunteerId()
                });
            }
        }
    }

    private void acceptSelected() {
        int sel = table.getSelectedRow();
        if (sel == -1) { JOptionPane.showMessageDialog(this, "Select a request first"); return; }
        int reqId = (int) model.getValueAt(sel, 0);
        Request r = DataStore.getRequestById(reqId);
        if (r == null) { JOptionPane.showMessageDialog(this, "Request not found"); return; }
        if (r.getAssignedVolunteerId() != null) {
            JOptionPane.showMessageDialog(this, "Request already assigned to a volunteer.");
            return;
        }
        r.assignVolunteer(volunteer.getId());
        r.setStatus("Assigned");
        DataStore.updateRequest(r);
        JOptionPane.showMessageDialog(this, "Request assigned to you. Admin will allocate resources if needed.");
        loadRequests();
    }

    private void escalateSelected() {
        int sel = table.getSelectedRow();
        if (sel == -1) { JOptionPane.showMessageDialog(this, "Select a request first"); return; }
        int reqId = (int) model.getValueAt(sel, 0);
        Request r = DataStore.getRequestById(reqId);
        if (r == null) { JOptionPane.showMessageDialog(this, "Request not found"); return; }
        r.setStatus("Escalated");
        DataStore.updateRequest(r);
        JOptionPane.showMessageDialog(this, "Request escalated to Admin.");
        loadRequests();
    }

    private void completeSelected() {
        int sel = table.getSelectedRow();
        if (sel == -1) { JOptionPane.showMessageDialog(this, "Select a request first"); return; }
        int reqId = (int) model.getValueAt(sel, 0);
        Request r = DataStore.getRequestById(reqId);
        if (r == null) { JOptionPane.showMessageDialog(this, "Request not found"); return; }
        if (r.getAssignedVolunteerId() == null) {
            JOptionPane.showMessageDialog(this, "This request is not assigned to any volunteer.");
            return;
        }
        if (!r.getAssignedVolunteerId().equals(volunteer.getId())) {
            JOptionPane.showMessageDialog(this, "You are not assigned to this request.");
            return;
        }
        r.setStatus("Completed");
        DataStore.updateRequest(r);
        JOptionPane.showMessageDialog(this, "Request marked as Completed.");
        loadRequests();
    }
}
