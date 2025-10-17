package Ui;
import javax.swing.*; 
import java.awt.*;
import java.awt.event.*;
import models.User;
import Ui.ResourceFrame;
import Ui.RequestFrame;
import Ui.AlertFrame;
import Ui.VolunteerRequestsFrame;
import Ui.AdminRequestsFrame;

public class DashboardFrame extends JFrame {
    private User user;

    public DashboardFrame(User user) {
        this.user = user;
        setTitle("AidBridge Dashboard - " + user.getRole());
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 2, 10, 10));

        if (user.getRole().equals("Admin")) {
            addButton("Add Resource", e -> new ResourceFrame(user));
            addButton("Send Alert", e -> new AlertFrame(user));
            addButton("Manage Requests", e -> new AdminRequestsFrame());
        } else if (user.getRole().equals("Volunteer")) {
            addButton("View Resources", e -> new ResourceFrame(user));
            addButton("View Pending Requests", e -> new VolunteerRequestsFrame(user));
        } else if (user.getRole().equals("Victim")) {
            addButton("Request Help", e -> new RequestFrame(user));
            addButton("View Resources", e -> new ResourceFrame(user));
        }

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addButton(String text, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.addActionListener(listener);
        add(btn);
    }
}
