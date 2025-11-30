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
    }
}