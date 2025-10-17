package Ui;
import javax.swing.*; 
import java.awt.*;
import java.util.List;
import models.Request;
import models.User;
import storages.DataStore;

public class RequestFrame extends JFrame {
    private User user;

    public RequestFrame(User user) {
        this.user = user;
        setTitle("Submit Help Request - AidBridge");
        setSize(400, 300);
        setLayout(new GridLayout(5, 2, 5, 5));

        JTextField typeField = new JTextField();
        JTextField locField = new JTextField();
        JComboBox<String> urgencyBox = new JComboBox<>(new String[]{"Low", "Medium", "High"});

        add(new JLabel("Request Type:"));
        add(typeField);
        add(new JLabel("Location:"));
        add(locField);
        add(new JLabel("Urgency:"));
        add(urgencyBox);

        JButton submitBtn = new JButton("Submit Request");
        add(submitBtn);

        submitBtn.addActionListener(e -> {
            String type = typeField.getText();
            String loc = locField.getText();
            String urgency = (String) urgencyBox.getSelectedItem();

            List<Request> requests = DataStore.loadRequests();
            int id = requests.size() + 1;
            requests.add(new Request(id, user.getId(), type, loc, urgency));
            DataStore.saveRequests(requests);
            JOptionPane.showMessageDialog(this, "Request Submitted!");
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
