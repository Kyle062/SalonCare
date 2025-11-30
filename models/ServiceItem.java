package models;

import java.io.Serializable;

public class ServiceItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id, name;
    private double price;

    public ServiceItem(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name + " — ₱" + price;
    }
}