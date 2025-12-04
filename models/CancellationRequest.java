package models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CancellationRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String requestId;
    private String appointmentId;
    private Client client;
    private ServiceItem service;
    private LocalDateTime appointmentDateTime;
    private LocalDateTime requestDateTime;
    private String reason;

    public CancellationRequest(String requestId, String appointmentId, Client client,
            ServiceItem service, LocalDateTime appointmentDateTime,
            String reason) {
        this.requestId = requestId;
        this.appointmentId = appointmentId;
        this.client = client;
        this.service = service;
        this.appointmentDateTime = appointmentDateTime;
        this.requestDateTime = LocalDateTime.now();
        this.reason = reason;
    }

    // Getters
    public String getRequestId() {
        return requestId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public Client getClient() {
        return client;
    }

    public ServiceItem getService() {
        return service;
    }

    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public LocalDateTime getRequestDateTime() {
        return requestDateTime;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "Cancellation Request ID: " + requestId +
                "\nClient: " + client.getName() +
                "\nService: " + service.getName() +
                "\nScheduled Date/Time: " + appointmentDateTime.format(
                        java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"))
                +
                "\nRequested At: " + requestDateTime.format(
                        java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"))
                +
                "\nReason: " + (reason.isEmpty() ? "No reason provided" : reason);
    }
}