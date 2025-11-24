package components;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;

    // Public fields for simplicity (easy to read for non-programmers).
    // In production code we would typically make these private with
    // getters/setters.
    public String id; // unique identifier (generated automatically)
    public String clientName; // client's full name
    public String contact; // contact information (phone or email)
    public String serviceType; // e.g., Haircut, Manicure
    public LocalDateTime dateTime; // appointment date & time

    // Display format used in the UI and for parsing input
    public static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Simple constructor
    public Appointment(String id, String clientName, String contact, String serviceType, LocalDateTime dateTime) {
        this.id = id;
        this.clientName = clientName;
        this.contact = contact;
        this.serviceType = serviceType;
        this.dateTime = dateTime;
    }

    // Human-readable description used in lists and logs
    @Override
    public String toString() {
        String dt = dateTime == null ? "(no date)" : dateTime.format(DISPLAY_FMT);
        return clientName + " — " + serviceType + " @ " + dt;
    }

    public String getClientName() {
        return clientName;
    }

    public String getContact() {
        return contact;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public static DateTimeFormatter getDisplayFmt() {
        return DISPLAY_FMT;
    }

    public String getId() {
        return id;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getServiceType() {
        return serviceType;
    }
}
