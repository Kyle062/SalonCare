package models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private Client client;
    private ServiceItem service;
    private LocalDateTime dateTime;
    private boolean confirmed;

    public Appointment(String id, Client client, ServiceItem service, LocalDateTime dateTime) {
        this.id = id;
        this.client = client;
        this.service = service;
        this.dateTime = dateTime;
        this.confirmed = false;
    }

    public String getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public ServiceItem getService() {
        return service;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setService(ServiceItem service) {
        this.service = service;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return id + " | " + client.getName() + " | " + service.getName() + " | " + dateTime.toString()
                + (confirmed ? " | CONFIRMED" : "");
    }
}