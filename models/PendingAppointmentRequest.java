package models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class PendingAppointmentRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String requestId; // Unique ID for the request
    private Client client;
    private ServiceItem service;
    private LocalDateTime preferredDateTime;
    private String clientMessage; // Optional message from client

    public PendingAppointmentRequest(String requestId, Client client, ServiceItem service,
            LocalDateTime preferredDateTime, String clientMessage) {
        this.requestId = requestId;
        this.client = client;
        this.service = service;
        this.preferredDateTime = preferredDateTime;
        this.clientMessage = clientMessage;
    }

    // Getters
    public String getRequestId() {
        return requestId;
    }

    public Client getClient() {
        return client;
    }

    public ServiceItem getService() {
        return service;
    }

    public LocalDateTime getPreferredDateTime() {
        return preferredDateTime;
    }

    public String getClientMessage() {
        return clientMessage;
    }

    @Override
    public String toString() {
        return "Request ID: " + requestId +
                "\nClient: " + client.getName() +
                "\nService: " + service.getName() +
                "\nDate/Time: "
                + preferredDateTime.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")) +
                "\nMessage: " + (clientMessage.isEmpty() ? "N/A" : clientMessage);
    }
}