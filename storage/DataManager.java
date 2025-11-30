package storage;

import models.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    public final List<Client> clients = new ArrayList<>();
    public final List<ServiceItem> services = new ArrayList<>();
    public final AppointmentLinkedList appointments = new AppointmentLinkedList();

    public DataManager() {
        seed();
    }

    private void seed() {
        clients.add(new Client("C001", "Alice", "09171234567", "alice@mail.com"));
        clients.add(new Client("C002", "Ben", "09179876543", "ben@mail.com"));

        services.add(new ServiceItem("S001", "Haircut", 200));
        services.add(new ServiceItem("S002", "Manicure", 350));
        services.add(new ServiceItem("S003", "Facial", 500));

        appointments
                .addSorted(new Appointment("A001", clients.get(0), services.get(0), LocalDateTime.now().plusDays(1)));
        appointments
                .addSorted(new Appointment("A002", clients.get(1), services.get(2), LocalDateTime.now().plusDays(2)));
    }
}