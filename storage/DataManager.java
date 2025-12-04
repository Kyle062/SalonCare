package storage;

import models.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataManager {
    public final List<Client> clients = new ArrayList<>();
    public final List<ServiceItem> services = new ArrayList<>();
    public final AppointmentLinkedList appointments = new AppointmentLinkedList();
    public final List<PendingAppointmentRequest> pendingRequests = new ArrayList<>();

    private static DataManager instance;

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public DataManager() {
        seed();
    }

    private void seed() {
        // Seed clients
        clients.add(new Client("C001", "Maria Santos", "09171234567", "maria@example.com", "pass123"));
        clients.add(new Client("C002", "John Cruz", "09187654321", "john@example.com", "pass123"));
        clients.add(new Client("C003", "Anna Lee", "09191112222", "anna@example.com", "pass123"));
        clients.add(new Client("C004", "Jielly Abao", "09123456789", "jiellyAbao@gmail.com", "password123"));

        // Seed services
        services.add(new ServiceItem("Signature Haircut", 800.00));
        services.add(new ServiceItem("Deep Cleansing Facial", 1500.00));
        services.add(new ServiceItem("Full Balayage Color", 3200.00));
        services.add(new ServiceItem("Classic Manicure", 950.00));
        services.add(new ServiceItem("Eyebrow Threading", 550.00));
        services.add(new ServiceItem("Relaxing Body Massage", 1800.00));
        services.add(new ServiceItem("Permanent Hair Straight", 4500.00));
        services.add(new ServiceItem("Basic Facial", 700.00));
        services.add(new ServiceItem("Hot Stone Massage", 2500.00));

        // Seed sample appointments
        Client maria = clients.get(0);
        Client john = clients.get(1);
        ServiceItem haircut = services.get(0);
        ServiceItem facial = services.get(1);

        Appointment appt1 = new Appointment(UUID.randomUUID().toString(), maria, haircut,
                LocalDateTime.of(2025, 12, 12, 10, 30));
        appt1.setConfirmed(true);

        Appointment appt2 = new Appointment(UUID.randomUUID().toString(), john, facial,
                LocalDateTime.of(2025, 12, 13, 14, 0));
        appt2.setConfirmed(false);

        appointments.addSorted(appt1);
        appointments.addSorted(appt2);
    }

    // --- Client Management ---
    public Client registerClient(String name, String phone, String email, String password) {
        // Check if email already exists
        for (Client client : clients) {
            if (client.getEmail().equalsIgnoreCase(email)) {
                return null; // Email already registered
            }
        }

        String id = "C" + String.format("%03d", clients.size() + 1);
        Client newClient = new Client(id, name, phone, email, password);
        clients.add(newClient);
        return newClient;
    }

    public Client authenticateClient(String emailOrPhone, String password) {
        for (Client client : clients) {
            if ((client.getEmail().equalsIgnoreCase(emailOrPhone) ||
                    client.getPhone().equals(emailOrPhone)) &&
                    client.getPassWord().equals(password)) {
                return client;
            }
        }
        return null;
    }

    public Client getClientByEmail(String email) {
        for (Client client : clients) {
            if (client.getEmail().equalsIgnoreCase(email)) {
                return client;
            }
        }
        return null;
    }

    public Client getClientByEmailOrPhone(String emailOrPhone) {
        for (Client client : clients) {
            if (client.getEmail().equalsIgnoreCase(emailOrPhone) ||
                    client.getPhone().equals(emailOrPhone)) {
                return client;
            }
        }
        return null;
    }

    // --- Service Management ---
    public ServiceItem getServiceByName(String name) {
        for (ServiceItem service : services) {
            if (service.getName().equalsIgnoreCase(name)) {
                return service;
            }
        }
        return null;
    }

    // --- Appointment Management ---
    public List<Appointment> getClientAppointments(String clientEmail) {
        List<Appointment> clientAppointments = new ArrayList<>();
        for (Appointment appt : appointments.toList()) {
            if (appt.getClient().getEmail().equalsIgnoreCase(clientEmail)) {
                clientAppointments.add(appt);
            }
        }
        return clientAppointments;
    }

    // --- Pending Requests ---
    public void addPendingRequest(PendingAppointmentRequest request) {
        pendingRequests.add(request);
    }

    public void removePendingRequest(String requestId) {
        pendingRequests.removeIf(req -> req.getRequestId().equals(requestId));
    }

    public List<PendingAppointmentRequest> getPendingRequests() {
        return new ArrayList<>(pendingRequests);
    }
}