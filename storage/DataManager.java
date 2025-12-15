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
    public final List<CancellationRequest> cancellationRequests = new ArrayList<>(); // NEW

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

                // Seed staff
                clients.add(new Client("S001", "Admin Staff", "09100000001", "staff@salon.com", "staff123", true));
                clients.add(new Client("S002", "Jane Smith", "09100000002", "staff2@salon.com", "staff123", true));

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
        appt2.setConfirmed(true);

        appointments.addSorted(appt1);
        appointments.addSorted(appt2);
    }

    // --- Helper methods to convert to List ---
    public List<ServiceItem> getServicesList() {
        return new ArrayList<>(services);
    }

    public List<Appointment> getAppointmentsList() {
        return appointments.toList();
    }

    public List<CancellationRequest> getCancellationRequestsList() {
        return new ArrayList<>(cancellationRequests);
    }

    // --- Client Management ---
    public Client registerClient(String name, String phone, String email, String password, boolean isStaff) {
        // Check if email already exists
        for (Client client : clients) {
            if (client.getEmail().equalsIgnoreCase(email)) {
                return null; // Email already registered
            }
        }

        String id;
        if (isStaff) {
            id = "S" + String.format("%03d", clients.stream().filter(c -> c.isStaff()).count() + 1);
        } else {
            id = "C" + String.format("%03d", clients.stream().filter(c -> !c.isStaff()).count() + 1);
        }

        Client newClient = new Client(id, name, phone, email, password, isStaff);
        clients.add(newClient);
        return newClient;
    }

    // Overloaded for regular client registration
    public Client registerClient(String name, String phone, String email, String password) {
        return registerClient(name, phone, email, password, false);
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

    // --- Cancellation Requests ---
    public void addCancellationRequest(CancellationRequest request) {
        cancellationRequests.add(request);
    }

    public void removeCancellationRequest(String requestId) {
        cancellationRequests.removeIf(req -> req.getRequestId().equals(requestId));
    }

    public void approveCancellation(String requestId) {
        CancellationRequest request = cancellationRequests.stream()
                .filter(req -> req.getRequestId().equals(requestId))
                .findFirst()
                .orElse(null);

        if (request != null) {
            appointments.removeById(request.getAppointmentId());
            cancellationRequests.remove(request);
        }
    }
}