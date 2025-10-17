package Ui;


import javax.swing.*; 
import java.awt.*;
import java.util.List;
import models.Alert;
import models.User;
import storages.DataStore;

public class AlertFrame extends JFrame {
    private models.User user;

    public AlertFrame(models.User user) {
        this.user = user;
        setTitle("Send Alert - AidBridge");
        setSize(400, 300);
        setLayout(new BorderLayout());

        JTextArea messageArea = new JTextArea();
        add(new JScrollPane(messageArea), BorderLayout.CENTER);

        JButton sendBtn = new JButton("Send Alert");
        add(sendBtn, BorderLayout.SOUTH);

        sendBtn.addActionListener(e -> {
            String message = messageArea.getText();
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter message first!");
                return;
            }
            List<models.Alert> alerts = DataStore.loadAlerts();
            int id = alerts.size() + 1;
            alerts.add(new models.Alert(id, message));
            DataStore.saveAlerts(alerts);

            JOptionPane.showMessageDialog(this, "Alert Sent to all users!");
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
