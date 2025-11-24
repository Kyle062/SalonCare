package components;
/*
File: AppointmentLinkedList.java
A readable singly-linked-list implementation that keeps appointments sorted by date/time.
It offers methods to add, update, delete, search, and list appointments.
This class hides the internal node logic so the GUI code is simple to understand.
*/

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class AppointmentLinkedList {

    private AppointmentNode head;

    // Add a new appointment to the end of the list
    public void addAppointment(Appointment appointment) {
        AppointmentNode newNode = new AppointmentNode(appointment);

        if (head == null) {
            head = newNode;
            return;
        }

        AppointmentNode current = head;
        while (current.next != null) {
            current = current.next;
        }

        current.next = newNode;
    }

    // Remove an appointment by index
    public boolean removeAppointment(int index) {
        if (head == null)
            return false;
        if (index == 0) {
            head = head.next;
            return true;
        }

        AppointmentNode current = head;
        int counter = 0;

        while (current != null && current.next != null) {
            if (counter + 1 == index) {
                current.next = current.next.next;
                return true;
            }
            current = current.next;
            counter++;
        }

        return false;
    }

    // Turn the linked list into a 2D array for the JTable
    public Object[][] toTableData() {
        int size = size();
        Object[][] data = new Object[size][5];

        AppointmentNode current = head;
        int i = 0;

        while (current != null) {
            data[i][0] = current.data.getClientName();
            data[i][1] = current.data.getServiceType();
            data[i][2] = current.data.getDateTime();
            data[i][3] = current.data.getClientName();
            data[i][4] = current.data.getContact();
            current = current.next;
            i++;
        }

        return data;
    }

    // Count items in the list
    public int size() {
        int count = 0;
        AppointmentNode current = head;
        while (current != null) {
            current = current.next;
            count++;
        }
        return count;
    }
}