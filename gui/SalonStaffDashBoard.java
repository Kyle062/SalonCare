package gui;

import models.*;
import storage.DataManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.Comparator;

public class SalonStaffDashboard extends JFrame {

    private DataManager dataManager = DataManager.getInstance();

    // UI Components
    private JPanel contentPane;
    private JTextField clientNameField;
    private JTextField contactNumberField;
    private JComboBox<String> serviceComboBox;
    private JFormattedTextField dateField;
    private JComboBox<String> hourComboBox;
    private JComboBox<String> minuteComboBox;
    private JComboBox<String> ampmComboBox;
    private JButton addAppointmentButton;
    private JButton updateExistingButton;
    private JTextField searchClientField;
    private JPanel scheduledAppointmentsPanel;
    private JPanel cancellationRequestsContainer; 
    private Appointment selectedAppointmentForEdit = null;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

    public SalonStaffDashboard() {
        setTitle("SalonCare System - Staff Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        Color themeColor = new Color(128, 207, 192);
        Color lightGreen = new Color(224, 247, 245);
        Color darkerGreen = new Color(100, 180, 170);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        contentPane.setBackground(lightGreen);

        // Top Panel for Title and Logout
        JPanel topPanel = new JPanel();
        topPanel.setBackground(themeColor);
        topPanel.setPreferredSize(new Dimension(getWidth(), 50));
        topPanel.setLayout(new BorderLayout());
        contentPane.add(topPanel, BorderLayout.NORTH);

        JLabel titleLabel = new JLabel("  SalonCare System - Staff Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("LOGOUT");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(darkerGreen);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        topPanel.add(logoutButton, BorderLayout.EAST);

        // Main content panel
        JPanel mainContentPanel = new JPanel(new GridBagLayout());
        mainContentPanel.setBackground(lightGreen);
        contentPane.add(mainContentPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // --- Left Panel: New Appointment Form ---
        JPanel newAppointmentPanel = new JPanel();
        newAppointmentPanel.setBackground(Color.WHITE);
        newAppointmentPanel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(themeColor, 2), "NEW APPOINTMENT FORM", 0, 0,
                new Font("Segoe UI", Font.BOLD, 16), themeColor));
        newAppointmentPanel.setLayout(new GridBagLayout());
        newAppointmentPanel.setPreferredSize(new Dimension(400, 600));

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(5, 5, 5, 5);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.weightx = 1.0;

        formGbc.gridx = 0;
        formGbc.gridy = 0;
        newAppointmentPanel.add(new JLabel("Client Name:"), formGbc);
        formGbc.gridy = 1;
        clientNameField = new JTextField(20);
        newAppointmentPanel.add(clientNameField, formGbc);

        formGbc.gridy = 2;
        newAppointmentPanel.add(new JLabel("Contact Number:"), formGbc);
        formGbc.gridy = 3;
        contactNumberField = new JTextField(20);
        newAppointmentPanel.add(contactNumberField, formGbc);

        formGbc.gridy = 4;
        newAppointmentPanel.add(new JLabel("Type of Service:"), formGbc);
        formGbc.gridy = 5;
        serviceComboBox = new JComboBox<>();
        loadServiceItems();
        newAppointmentPanel.add(serviceComboBox, formGbc);

        formGbc.gridy = 6;
        newAppointmentPanel.add(new JLabel("Appointment Date:"), formGbc);
        formGbc.gridy = 7;
        dateField = new JFormattedTextField(new java.text.SimpleDateFormat("MM/dd/yyyy"));
        dateField.setValue(java.sql.Date.valueOf(LocalDate.now()));
        dateField.setColumns(10);
        newAppointmentPanel.add(dateField, formGbc);

        formGbc.gridy = 8;
        newAppointmentPanel.add(new JLabel("Time:"), formGbc);
        formGbc.gridy = 9;
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        timePanel.setBackground(Color.WHITE);
        hourComboBox = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            hourComboBox.addItem(String.format("%02d", i));
        }
        hourComboBox.setSelectedItem(
                String.format("%02d", LocalTime.now().getHour() % 12 == 0 ? 12 : LocalTime.now().getHour() % 12));
        timePanel.add(hourComboBox);
        timePanel.add(new JLabel(":"));
        minuteComboBox = new JComboBox<>();
        for (int i = 0; i < 60; i += 5) {
            minuteComboBox.addItem(String.format("%02d", i));
        }
        minuteComboBox.setSelectedItem(String.format("%02d", (LocalTime.now().getMinute() / 5) * 5));
        timePanel.add(minuteComboBox);
        ampmComboBox = new JComboBox<>(new String[] { "AM", "PM" });
        ampmComboBox.setSelectedItem(LocalTime.now().getHour() >= 12 ? "PM" : "AM");
        timePanel.add(ampmComboBox);
        newAppointmentPanel.add(timePanel, formGbc);

        formGbc.gridy = 10;
        formGbc.ipady = 10;
        formGbc.insets = new Insets(20, 5, 5, 5);
        addAppointmentButton = new JButton("ADD APPOINTMENT");
        addAppointmentButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addAppointmentButton.setBackground(darkerGreen);
        addAppointmentButton.setForeground(Color.WHITE);
        addAppointmentButton.setFocusPainted(false);
        newAppointmentPanel.add(addAppointmentButton, formGbc);

        formGbc.gridy = 11;
        formGbc.insets = new Insets(5, 5, 20, 5);
        updateExistingButton = new JButton("UPDATE EXISTING");
        updateExistingButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateExistingButton.setBackground(themeColor.darker());
        updateExistingButton.setForeground(Color.WHITE);
        updateExistingButton.setFocusPainted(false);
        updateExistingButton.setEnabled(false);
        newAppointmentPanel.add(updateExistingButton, formGbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 1.0;
        mainContentPanel.add(newAppointmentPanel, gbc);

        // --- Right Panel: Scheduled Appointments and Cancellation Requests ---
        JPanel rightSidePanel = new JPanel(new GridBagLayout());
        rightSidePanel.setBackground(lightGreen);

        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.insets = new Insets(0, 0, 10, 0);

        // Search Bar
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBackground(Color.WHITE);
        searchClientField = new JTextField(25);
        searchClientField.putClientProperty("JTextField.placeholderText", "Search Client...");
        searchClientField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(darkerGreen);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> refreshScheduledAppointments(searchClientField.getText()));

        searchPanel.add(searchClientField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        searchPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        rightGbc.gridx = 0;
        rightGbc.gridy = 0;
        rightGbc.weightx = 1.0;
        rightGbc.fill = GridBagConstraints.HORIZONTAL;
        rightSidePanel.add(searchPanel, rightGbc);

        // Scheduled Appointments
        scheduledAppointmentsPanel = new JPanel();
        scheduledAppointmentsPanel.setLayout(new BoxLayout(scheduledAppointmentsPanel, BoxLayout.Y_AXIS));
        scheduledAppointmentsPanel.setBackground(Color.WHITE);
        JScrollPane scheduledScrollPane = new JScrollPane(scheduledAppointmentsPanel);
        scheduledScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scheduledScrollPane.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(themeColor, 2), "SCHEDULED APPOINTMENTS", 0, 0,
                new Font("Segoe UI", Font.BOLD, 16), themeColor));
        scheduledScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        rightGbc.gridx = 0;
        rightGbc.gridy = 1;
        rightGbc.weighty = 0.6;
        rightGbc.fill = GridBagConstraints.BOTH;
        rightSidePanel.add(scheduledScrollPane, rightGbc);

        // Cancellation Requests Panel
        cancellationRequestsContainer = new JPanel();
        cancellationRequestsContainer.setLayout(new BoxLayout(cancellationRequestsContainer, BoxLayout.Y_AXIS));
        cancellationRequestsContainer.setBackground(Color.WHITE);

        JScrollPane cancellationScrollPane = new JScrollPane(cancellationRequestsContainer);
        cancellationScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        cancellationScrollPane.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(255, 165, 0), 2), "PENDING CANCELLATION REQUESTS", 0, 0,
                new Font("Segoe UI", Font.BOLD, 16), new Color(255, 165, 0)));
        cancellationScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        rightGbc.gridx = 0;
        rightGbc.gridy = 2;
        rightGbc.weighty = 0.4;
        rightGbc.fill = GridBagConstraints.BOTH;
        rightSidePanel.add(cancellationScrollPane, rightGbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;
        mainContentPanel.add(rightSidePanel, gbc);

        // Initial load
        refreshScheduledAppointments(null);
        refreshCancellationRequests();
        addListeners();
    }

    private void loadServiceItems() {
        for (ServiceItem service : dataManager.services) {
            serviceComboBox.addItem(service.getName());
        }
    }

    private void addListeners() {
        addAppointmentButton.addActionListener(this::addAppointment);
        updateExistingButton.addActionListener(this::updateAppointment);
    }

    private void addAppointment(ActionEvent e) {
        String clientName = clientNameField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();
        String serviceName = (String) serviceComboBox.getSelectedItem();
        String hourStr = (String) hourComboBox.getSelectedItem();
        String minuteStr = (String) minuteComboBox.getSelectedItem();
        String ampm = (String) ampmComboBox.getSelectedItem();

        if (clientName.isEmpty() || contactNumber.isEmpty() || serviceName == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all appointment details.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Client client = dataManager.getClientByEmailOrPhone(contactNumber);
        if (client == null) {
            client = new Client(UUID.randomUUID().toString(), clientName, contactNumber, "", "");
            dataManager.clients.add(client);
        }

        ServiceItem service = dataManager.getServiceByName(serviceName);
        if (service == null) {
            JOptionPane.showMessageDialog(this, "Selected service is invalid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDateTime appointmentDateTime;
        try {
            int hour = Integer.parseInt(hourStr);
            if (ampm.equals("PM") && hour != 12) {
                hour += 12;
            } else if (ampm.equals("AM") && hour == 12) {
                hour = 0;
            }
            LocalTime time = LocalTime.of(hour, Integer.parseInt(minuteStr));

            java.util.Date dateValue = (java.util.Date) dateField.getValue();
            if (dateValue == null) {
                JOptionPane.showMessageDialog(this, "Please enter a valid date.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate date = dateValue.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            appointmentDateTime = LocalDateTime.of(date, time);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date or time format: " + ex.getMessage(), "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check for conflicts
        if (dataManager.appointments.hasConflict(client, appointmentDateTime)) {
            JOptionPane.showMessageDialog(this, "This client already has an appointment at this exact time.",
                    "Scheduling Conflict", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Appointment newAppointment = new Appointment(UUID.randomUUID().toString(), client, service,
                appointmentDateTime);
        newAppointment.setConfirmed(true);
        dataManager.appointments.addSorted(newAppointment);
        JOptionPane.showMessageDialog(this, "Appointment added successfully!", "Success",
                JOptionPane.INFORMATION_MESSAGE);
        clearForm();
        refreshScheduledAppointments(null);
    }

    private void updateAppointment(ActionEvent e) {
        if (selectedAppointmentForEdit == null) {
            JOptionPane.showMessageDialog(this, "No appointment selected for update.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String clientName = clientNameField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();
        String serviceName = (String) serviceComboBox.getSelectedItem();
        String hourStr = (String) hourComboBox.getSelectedItem();
        String minuteStr = (String) minuteComboBox.getSelectedItem();
        String ampm = (String) ampmComboBox.getSelectedItem();

        if (clientName.isEmpty() || contactNumber.isEmpty() || serviceName == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all appointment details.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Client client = dataManager.getClientByEmailOrPhone(contactNumber);
        if (client == null) {
            client = new Client(UUID.randomUUID().toString(), clientName, contactNumber, "", "");
            dataManager.clients.add(client);
        } else if (!client.getId().equals(selectedAppointmentForEdit.getClient().getId())
                && !client.getName().equalsIgnoreCase(clientName)) {
            int response = JOptionPane.showConfirmDialog(this,
                    "Contact number matches a different existing client (" + client.getName()
                            + "). Do you want to change this appointment's client to the matching one, or update the current client's contact?",
                    "Client Conflict", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                selectedAppointmentForEdit.setClient(client);
            } else if (response == JOptionPane.NO_OPTION) {
                Client originalClient = selectedAppointmentForEdit.getClient();
                originalClient.setName(clientName);
                originalClient.setPhone(contactNumber);
                client = originalClient;
            } else {
                return;
            }
        } else {
            selectedAppointmentForEdit.getClient().setName(clientName);
            selectedAppointmentForEdit.getClient().setPhone(contactNumber);
        }

        ServiceItem service = dataManager.getServiceByName(serviceName);
        if (service == null) {
            JOptionPane.showMessageDialog(this, "Selected service is invalid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDateTime appointmentDateTime;
        try {
            int hour = Integer.parseInt(hourStr);
            if (ampm.equals("PM") && hour != 12) {
                hour += 12;
            } else if (ampm.equals("AM") && hour == 12) {
                hour = 0;
            }
            LocalTime time = LocalTime.of(hour, Integer.parseInt(minuteStr));

            java.util.Date dateValue = (java.util.Date) dateField.getValue();
            if (dateValue == null) {
                JOptionPane.showMessageDialog(this, "Please enter a valid date.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate date = dateValue.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            appointmentDateTime = LocalDateTime.of(date, time);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date or time format: " + ex.getMessage(), "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!selectedAppointmentForEdit.getDateTime().equals(appointmentDateTime)
                || !selectedAppointmentForEdit.getClient().getId().equals(client.getId())) {
            for (Appointment existingAppt : dataManager.appointments.toList()) {
                if (!existingAppt.getId().equals(selectedAppointmentForEdit.getId()) &&
                        existingAppt.getClient().getId().equals(client.getId()) &&
                        existingAppt.getDateTime().equals(appointmentDateTime)) {
                    JOptionPane.showMessageDialog(this,
                            "This client already has another appointment at this exact new time.",
                            "Scheduling Conflict", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        }

        selectedAppointmentForEdit.setClient(client);
        selectedAppointmentForEdit.setService(service);
        selectedAppointmentForEdit.setDateTime(appointmentDateTime);

        dataManager.appointments.removeById(selectedAppointmentForEdit.getId());
        dataManager.appointments.addSorted(selectedAppointmentForEdit);

        JOptionPane.showMessageDialog(this, "Appointment updated successfully!", "Success",
                JOptionPane.INFORMATION_MESSAGE);
        clearForm();
        refreshScheduledAppointments(null);
        selectedAppointmentForEdit = null;
        updateExistingButton.setEnabled(false);
        addAppointmentButton.setEnabled(true);
    }

    private void refreshScheduledAppointments(String searchText) {
        scheduledAppointmentsPanel.removeAll();
        List<Appointment> currentAppointments;

        if (searchText == null || searchText.trim().isEmpty()) {
            currentAppointments = dataManager.appointments.toList();
        } else {
            currentAppointments = dataManager.appointments.searchByClientName(searchText);
        }

        if (currentAppointments.isEmpty()) {
            JLabel noAppointmentsLabel = new JLabel("No scheduled appointments found.", SwingConstants.CENTER);
            noAppointmentsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            noAppointmentsLabel.setForeground(Color.GRAY);
            noAppointmentsLabel.setBorder(new EmptyBorder(50, 0, 50, 0));
            scheduledAppointmentsPanel.add(noAppointmentsLabel);
        } else {
            for (int i = 0; i < currentAppointments.size(); i++) {
                Appointment appointment = currentAppointments.get(i);
                scheduledAppointmentsPanel.add(createAppointmentCard(appointment));
                if (i < currentAppointments.size() - 1) {
                    scheduledAppointmentsPanel.add(createArrowLabel());
                }
            }
        }
        scheduledAppointmentsPanel.revalidate();
        scheduledAppointmentsPanel.repaint();
    }

    private void refreshCancellationRequests() {
        cancellationRequestsContainer.removeAll();

        List<CancellationRequest> requests = dataManager.getCancellationRequestsList();
        requests.sort(Comparator.comparing(CancellationRequest::getRequestDateTime));

        if (requests.isEmpty()) {
            JLabel noRequestsLabel = new JLabel("No pending cancellation requests.", SwingConstants.CENTER);
            noRequestsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            noRequestsLabel.setForeground(Color.GRAY);
            noRequestsLabel.setBorder(new EmptyBorder(50, 0, 50, 0));
            cancellationRequestsContainer.add(noRequestsLabel);
        } else {
            for (CancellationRequest request : requests) {
                cancellationRequestsContainer.add(createCancellationRequestCard(request));
            }
        }
        cancellationRequestsContainer.revalidate();
        cancellationRequestsContainer.repaint();
    }

    private JPanel createAppointmentCard(Appointment appointment) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(new LineBorder(new Color(192, 192, 192), 1, true));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel detailsPanel = new JPanel(new GridLayout(4, 1));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(new EmptyBorder(5, 10, 5, 5));
        detailsPanel.add(new JLabel("Name: " + appointment.getClientName()));
        detailsPanel.add(new JLabel("Service: " + appointment.getServiceName()));
        detailsPanel.add(new JLabel("Date: " + appointment.getDateTime().format(DATE_TIME_FORMATTER).split(" ")[0] + " "
                + appointment.getDateTime().format(DATE_TIME_FORMATTER).split(" ")[1]));
        detailsPanel.add(new JLabel("Time: " + appointment.getDateTime().format(DATE_TIME_FORMATTER).split(" ")[2] + " "
                + appointment.getDateTime().format(DATE_TIME_FORMATTER).split(" ")[3]));

        card.add(detailsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonPanel.setBackground(Color.WHITE);
        JButton editButton = new JButton("EDIT");
        editButton.setBackground(new Color(60, 179, 113));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> populateFormForEdit(appointment));

        JButton cancelButton = new JButton("CANCEL");
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> confirmCancelAppointment(appointment));

        buttonPanel.add(editButton);
        buttonPanel.add(cancelButton);
        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }

    private JLabel createArrowLabel() {
        JLabel arrowLabel = new JLabel("<html><center>&darr;</center></html>", SwingConstants.CENTER);
        arrowLabel.setFont(new Font("Arial", Font.BOLD, 24));
        arrowLabel.setForeground(new Color(150, 150, 150));
        arrowLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        arrowLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
        arrowLabel.setOpaque(true);
        arrowLabel.setBackground(new Color(245, 245, 245));
        return arrowLabel;
    }

    private JPanel createCancellationRequestCard(CancellationRequest request) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(new LineBorder(new Color(255, 165, 0), 1, true));
        card.setBackground(new Color(255, 250, 240));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        JPanel detailsPanel = new JPanel(new GridLayout(5, 1));
        detailsPanel.setBackground(card.getBackground());
        detailsPanel.setBorder(new EmptyBorder(5, 10, 5, 5));
        detailsPanel.add(new JLabel("Client: " + request.getClient().getName()));
        detailsPanel.add(new JLabel("Service: " + request.getService().getName()));
        detailsPanel.add(new JLabel("Scheduled: " + request.getAppointmentDateTime().format(DATE_TIME_FORMATTER)));
        detailsPanel.add(new JLabel("Requested: " + request.getRequestDateTime().format(DATE_TIME_FORMATTER)));
        detailsPanel.add(new JLabel("Reason: " + request.getReason()));

        card.add(detailsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonPanel.setBackground(card.getBackground());

        JButton approveButton = new JButton("Approve");
        approveButton.setBackground(new Color(34, 139, 34));
        approveButton.setForeground(Color.WHITE);
        approveButton.setFocusPainted(false);
        approveButton.addActionListener(e -> approveCancellation(request));

        JButton rejectButton = new JButton("Reject");
        rejectButton.setBackground(new Color(178, 34, 34));
        rejectButton.setForeground(Color.WHITE);
        rejectButton.setFocusPainted(false);
        rejectButton.addActionListener(e -> rejectCancellation(request));

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }

    private void approveCancellation(CancellationRequest request) {
        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to approve the cancellation request from " +
                        request.getClient().getName() + "?\n\nThis will remove the appointment permanently.",
                "Confirm Cancellation Approval",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            dataManager.approveCancellation(request.getRequestId());
            JOptionPane.showMessageDialog(this,
                    "Cancellation approved. Appointment removed from schedule.",
                    "Cancellation Approved",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshScheduledAppointments(null);
            refreshCancellationRequests();
        }
    }

    private void rejectCancellation(CancellationRequest request) {
        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reject the cancellation request from " +
                        request.getClient().getName() + "?\n\nThe appointment will remain scheduled.",
                "Confirm Cancellation Rejection",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            dataManager.removeCancellationRequest(request.getRequestId());
            JOptionPane.showMessageDialog(this,
                    "Cancellation request rejected. Appointment remains scheduled.",
                    "Cancellation Rejected",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshCancellationRequests();
        }
    }

    private void populateFormForEdit(Appointment appointment) {
        selectedAppointmentForEdit = appointment;
        clientNameField.setText(appointment.getClientName());
        contactNumberField.setText(appointment.getClientContact());
        serviceComboBox.setSelectedItem(appointment.getServiceName());

        dateField.setValue(java.sql.Date.valueOf(appointment.getDateTime().toLocalDate()));

        LocalTime time = appointment.getDateTime().toLocalTime();
        int hour = time.getHour();
        String ampm = "AM";
        if (hour >= 12) {
            ampm = "PM";
            if (hour > 12)
                hour -= 12;
        }
        if (hour == 0)
            hour = 12;

        hourComboBox.setSelectedItem(String.format("%02d", hour));
        minuteComboBox.setSelectedItem(String.format("%02d", (time.getMinute() / 5) * 5));
        ampmComboBox.setSelectedItem(ampm);

        addAppointmentButton.setEnabled(false);
        updateExistingButton.setEnabled(true);
    }

    private void confirmCancelAppointment(Appointment appointment) {
        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel the appointment for " + appointment.getClientName() + " on "
                        + appointment.getDateTime().format(DATE_TIME_FORMATTER) + "?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            dataManager.appointments.removeById(appointment.getId());
            JOptionPane.showMessageDialog(this, "Appointment cancelled successfully.", "Cancellation",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            refreshScheduledAppointments(null);
            selectedAppointmentForEdit = null;
            updateExistingButton.setEnabled(false);
            addAppointmentButton.setEnabled(true);
        }
    }

    private void clearForm() {
        clientNameField.setText("");
        contactNumberField.setText("");
        serviceComboBox.setSelectedIndex(0);
        dateField.setValue(java.sql.Date.valueOf(LocalDate.now()));
        hourComboBox.setSelectedItem(
                String.format("%02d", LocalTime.now().getHour() % 12 == 0 ? 12 : LocalTime.now().getHour() % 12));
        minuteComboBox.setSelectedItem(String.format("%02d", (LocalTime.now().getMinute() / 5) * 5));
        ampmComboBox.setSelectedItem(LocalTime.now().getHour() >= 12 ? "PM" : "AM");

        selectedAppointmentForEdit = null;
        updateExistingButton.setEnabled(false);
        addAppointmentButton.setEnabled(true);
    }

    public static void main(String[] args) {
        DataManager.getInstance();
        SwingUtilities.invokeLater(() -> new SalonStaffDashboard().setVisible(true));
    }
}