package models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class AppointmentLinkedList implements Serializable {
    private static final long serialVersionUID = 1L;

    private static class Node implements Serializable {
        Appointment data;
        Node next;

        Node(Appointment a) {
            data = a;
        }
    }

    private Node head;
    private int size = 0;

    public int size() {
        return size;
    }

    // insert sorted by datetime (earliest first)
    public void addSorted(Appointment appointment) {
        Node node = new Node(appointment);
        if (head == null || appointment.getDateTime().isBefore(head.data.getDateTime())) {
            node.next = head;
            head = node;
        } else {
            Node cur = head;
            while (cur.next != null && !appointment.getDateTime().isBefore(cur.next.data.getDateTime()))
                cur = cur.next;
            node.next = cur.next;
            cur.next = node;
        }
        size++;
    }

    public boolean removeById(String id) {
        if (head == null)
            return false;
        if (head.data.getId().equals(id)) {
            head = head.next;
            size--;
            return true;
        }
        Node cur = head;
        while (cur.next != null) {
            if (cur.next.data.getId().equals(id)) {
                cur.next = cur.next.next;
                size--;
                return true;
            }
            cur = cur.next;
        }
        return false;
    }

    public Appointment findById(String id) {
        Node cur = head;
        while (cur != null) {
            if (cur.data.getId().equals(id))
                return cur.data;
            cur = cur.next;
        }
        return null;
    }

    public List<Appointment> toList() {
        List<Appointment> list = new ArrayList<>();
        Node cur = head;
        while (cur != null) {
            list.add(cur.data);
            cur = cur.next;
        }
        return list;
    }

    public List<Appointment> filter(Predicate<Appointment> predicate) {
        List<Appointment> list = new ArrayList<>();
        Node cur = head;
        while (cur != null) {
            if (predicate.test(cur.data))
                list.add(cur.data);
            cur = cur.next;
        }
        return list;
    }

    public boolean hasConflict(Client client, LocalDateTime dt) {
        Node cur = head;
        while (cur != null) {
            if (cur.data.getClient().getId().equals(client.getId()) && cur.data.getDateTime().equals(dt))
                return true;
            cur = cur.next;
        }
        return false;
    }

    public List<Appointment> searchByClientName(String namePart) {
        List<Appointment> result = new ArrayList<>();
        Node cur = head;
        String key = namePart == null ? "" : namePart.toLowerCase();
        while (cur != null) {
            if (cur.data.getClient().getName().toLowerCase().contains(key))
                result.add(cur.data);
            cur = cur.next;
        }
        return result;
    }
}