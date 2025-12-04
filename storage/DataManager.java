package storage;

import models.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // For generating unique IDs

public class DataManager {
    public final List<Client> clients = new ArrayList<>();
    public final List<ServiceItem> services = new ArrayList<>();
    public final AppointmentLinkedList appointments = new AppointmentLinkedList();
    public final List<PendingAppointmentRequest> pendingRequests = new ArrayList<>(); // New list for pending requests

    public DataManager() {
        seed();
    }

    private void seed() {
        // Seed some clients
        Client client1 = new Client("C001", "Maria Santos", "09171234567", "maria@example.com", "pass123");
        Client client2 = new Client("C002", "John Cruz", "09187654321", "john@example.com", "pass123");
        Client client3 = new Client("C003", "Anna Lee", "09191112222", "anna@example.com", "pass123");
        clients.add(client1);
        clients.add(client2);
        clients.add(client3);

        // Seed some services
        ServiceItem service1 = new ServiceItem("Haircut", 250.00);
        ServiceItem service2 = new ServiceItem("Manicure", 150.00);
        ServiceItem service3 = new ServiceItem("Facial", 500.00);
        ServiceItem service4 = new ServiceItem("Hair Dye", 1000.00);
        services.add(service1);
        services.add(service2);
        services.add(service3);
        services.add(service4);

        // Seed some initial appointments (for demonstration of sorting)
        appointments.addSorted(new Appointment(UUID.randomUUID().toString(), client1, service1,
                LocalDateTime.of(2025, 12, 12, 10, 30)));
        appointments.addSorted(new Appointment(UUID.randomUUID().toString(), client2, service3,
                LocalDateTime.of(2025, 12, 12, 13, 0)));
        appointments.addSorted(
                new Appointment(UUID.randomUUID().toString(), client3, service2, LocalDateTime.of(2025, 12, 13, 9, 0)));
        appointments.addSorted(new Appointment(UUID.randomUUID().toString(), client1, service4,
                LocalDateTime.of(2025, 12, 11, 16, 0))); // Earlier appointment
    }

    // Methods to manage pending requests
    public void addPendingRequest(PendingAppointmentRequest request) {
        this.pendingRequests.add(request);
        System.out.println("New pending request added: " + request.getRequestId());
    }

    public void removePendingRequest(String requestId) {
        pendingRequests.removeIf(req -> req.getRequestId().equals(requestId));
        System.out.println("Pending request removed: " + requestId);
    }

    // This method will be used by ClientDashboardFrame to find a client for the
    // request
    public Client getClientByEmailOrPhone(String emailOrPhone) {
        for (Client client : clients) {
            if (client.getEmail().equalsIgnoreCase(emailOrPhone) || client.getPhone().equals(emailOrPhone)) {
                return client;
            }
        }
        return null;
    }

    // This method will be used to find a service by name
    public ServiceItem getServiceByName(String serviceName) {
        for (ServiceItem service : services) {
            if (service.getName().equalsIgnoreCase(serviceName)) {
                return service;
            }
        }
        return null;
    }

    // Singleton instance to ensure all frames use the same data
    private static DataManager instance;

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
}