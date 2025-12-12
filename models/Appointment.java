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
    private String cancellationStatus; // NEW: null, "PENDING", "APPROVED", "REJECTED"

    public Appointment(String id, Client client, ServiceItem service, LocalDateTime dateTime) {
        this.id = id;
        this.client = client;
        this.service = service;
        this.dateTime = dateTime;
        this.confirmed = false;
        this.cancellationStatus = null; // No cancellation request initially
    }

    // --- Getters ---
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

    public String getCancellationStatus() { // NEW
        return cancellationStatus;
    }

    public String getClientName() {
        return client.getName();
    }

    public String getClientContact() {
        return client.getPhone();
    }

    public String getServiceName() {
        return service.getName();
    }

    // --- Setters ---
    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public void setCancellationStatus(String cancellationStatus) { // NEW
        this.cancellationStatus = cancellationStatus;
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

    public boolean hasPendingCancellation() { // NEW helper method
        return "PENDING".equals(cancellationStatus);
    }

    @Override
    public String toString() {
        return id + " | " + client.getName() + " | " + service.getName() + " | " + dateTime.toString()
                + (confirmed ? " | CONFIRMED" : " | PENDING")
                + (cancellationStatus != null ? " | CANCELLATION: " + cancellationStatus : "");
    }
}