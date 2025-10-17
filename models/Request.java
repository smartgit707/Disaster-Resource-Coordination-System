package models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;           // victim who created the request
    private String type;
    private String location;
    private String urgency;

    // New fields for workflow
    private String status;        // e.g., "Pending", "Escalated", "Assigned", "Allocated", "Completed"
    private Integer assignedVolunteerId; // nullable: volunteer assigned to handle
    private Integer allocatedResourceId; // nullable: resource allocated
    private String adminNotes;    // any notes from admin
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Request(int id, int userId, String type, String location, String urgency) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.location = location;
        this.urgency = urgency;
        this.status = "Pending";
        this.assignedVolunteerId = null;
        this.allocatedResourceId = null;
        this.adminNotes = "";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    // getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getType() { return type; }
    public String getLocation() { return location; }
    public String getUrgency() { return urgency; }

    public String getStatus() { return status; }
    public Integer getAssignedVolunteerId() { return assignedVolunteerId; }
    public Integer getAllocatedResourceId() { return allocatedResourceId; }
    public String getAdminNotes() { return adminNotes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // setters and helpers (update updatedAt whenever state changes)
    public void setType(String type) { this.type = type; touch(); }
    public void setLocation(String location) { this.location = location; touch(); }
    public void setUrgency(String urgency) { this.urgency = urgency; touch(); }

    public void setStatus(String status) { this.status = status; touch(); }
    public void assignVolunteer(Integer volunteerId) { this.assignedVolunteerId = volunteerId; touch(); }
    public void allocateResource(Integer resourceId) { this.allocatedResourceId = resourceId; touch(); }
    public void setAdminNotes(String notes) { this.adminNotes = notes; touch(); }

    private void touch() { this.updatedAt = LocalDateTime.now(); }
}
