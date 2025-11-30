package models;

import java.io.Serializable;

public class Client implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id, name, phone, email , passWord; // keep basic

    public Client(String id, String name, String phone, String email , String passWord ) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.passWord = passWord;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}