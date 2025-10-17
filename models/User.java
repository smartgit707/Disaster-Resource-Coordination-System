package models;

import java.io.Serializable;

/**
 * Represents a user (Admin, Volunteer, Victim) in the AidBridge system.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private String role; // "Admin", "Volunteer", "Victim"
    private String contact; // Mobile number as String

    /**
     * Constructs a User object.
     * @param id unique user id
     * @param name user's name
     * @param role user's role ("Admin", "Volunteer", "Victim")
     * @param contact user's phone number
     */
    public User(int id, String name, String role, String contact) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.contact = contact;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getContact() { return contact; }

    public void setName(String name) { this.name = name; }
    public void setRole(String role) { this.role = role; }
    public void setContact(String contact) { this.contact = contact; }

    @Override
    public String toString() {
        return String.format("User[id=%d, name=%s, role=%s, contact=%s]", id, name, role, contact);
    }
}
