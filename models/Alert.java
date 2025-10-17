package models;


import java.io.Serializable;
import java.time.LocalDateTime;

public class Alert implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String message;
    private LocalDateTime timestamp;

    public Alert(int id, String message) {
        this.id = id;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public int getId() { return id; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
