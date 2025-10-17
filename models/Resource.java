package models;


import java.io.Serializable;

public class Resource implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String type;
    private int quantity;
    private String location;

    public Resource(int id, String type, int quantity, String location) {
        this.id = id;
        this.type = type;
        this.quantity = quantity;
        this.location = location;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public int getQuantity() { return quantity; }
    public String getLocation() { return location; }

    public void setType(String type) { this.type = type; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setLocation(String location) { this.location = location; }
}
