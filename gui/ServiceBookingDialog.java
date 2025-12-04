// Create a new class: gui/ServiceBookingDialog.java
package gui;

import models.*;
import storage.DataManager;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ServiceBookingDialog extends JDialog {
    private Client client;
    private ServiceItem selectedService;
    private JTextArea messageArea;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    public ServiceBookingDialog(JFrame parent, Client client, ServiceItem service) {
        super(parent, "Book Appointment", true);
        this.client = client;
        this.selectedService = service;

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(new JLabel("Book Service: " + selectedService.getName(),
                SwingConstants.CENTER), BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Date Selection
        formPanel.add(new JLabel("Preferred Date:"));
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "MM/dd/yyyy");
        dateSpinner.setEditor(dateEditor);
        formPanel.add(dateSpinner);

        // Time Selection
        formPanel.add(new JLabel("Preferred Time:"));
        timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "hh:mm a");
        timeSpinner.setEditor(timeEditor);
        formPanel.add(timeSpinner);

        // Message
        formPanel.add(new JLabel("Special Requests:"));
        messageArea = new JTextArea(3, 20);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        formPanel.add(scrollPane);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton submitButton = new JButton("Submit Request");
        JButton cancelButton = new JButton("Cancel");

        submitButton.addActionListener(e -> submitRequest());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void submitRequest() {
        try {
            java.util.Date date = (java.util.Date) dateSpinner.getValue();
            java.util.Date time = (java.util.Date) timeSpinner.getValue();

            // Combine date and time
            LocalDateTime preferredDateTime = LocalDateTime.of(
                    new java.sql.Date(date.getTime()).toLocalDate(),
                    new java.sql.Time(time.getTime()).toLocalTime());

            String message = messageArea.getText().trim();

            // Create pending request
            PendingAppointmentRequest request = new PendingAppointmentRequest(
                    UUID.randomUUID().toString(),
                    client,
                    selectedService,
                    preferredDateTime,
                    message);

            // Add to DataManager
            DataManager.getInstance().addPendingRequest(request);

            JOptionPane.showMessageDialog(this,
                    "Appointment request submitted! Staff will review and confirm.",
                    "Request Submitted",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error submitting request: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}