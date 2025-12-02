package models;

import java.io.Serializable;

public class ServiceItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private double price;

    public ServiceItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
    
    // Setter needed for the update logic
    public void setName(String name) {
        this.name = name;
    }
}