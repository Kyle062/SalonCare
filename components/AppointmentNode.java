package components;

/*
File: AppointmentNode.java
A small helper class used only by the linked list implementation.
Non-programmers: think of this as a "box" that holds one appointment and a pointer
to the next box.
*/

import java.io.Serializable;

class AppointmentNode implements Serializable {
    private static final long serialVersionUID = 1L;
    public Appointment data; // the appointment stored in this node
    public AppointmentNode next; // pointer to the next node (or null)

    public AppointmentNode(Appointment data) {
        this.data = data;
    }
}
