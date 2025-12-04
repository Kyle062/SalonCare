package gui;

import models.Appointment;
import models.Client;
import models.PendingAppointmentRequest;
import models.ServiceItem;
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
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.Comparator; // For sorting pending requests

public class SalonStaffDashboard extends JFrame {

    private DataManager dataManager = DataManager.getInstance(); // Get singleton instance

    // UI Components for the Staff Dashboard
    private JPanel contentPane;
    private JTextField clientNameField;
    private JTextField contactNumberField;
    private JComboBox<String> serviceComboBox;
    private JFormattedTextField dateField; // Using JFormattedTextField for date
    private JComboBox<String> hourComboBox;
    private JComboBox<String> minuteComboBox;
    private JComboBox<String> ampmComboBox;

    private JButton addAppointmentButton;
    private JButton updateExistingButton;
    private JTextField searchClientField;
    private JPanel scheduledAppointmentsPanel; // Panel to hold appointment cards
    private JPanel pendingRequestsPanel; // Panel to hold pending request cards
    private JPanel pendingRequestsContainer; // Scrollable container for pending requests

    private Appointment selectedAppointmentForEdit = null; // To hold the appointment being edited

    // Date formatter for display
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
    private static final DateTimeFormatter DATE_INPUT_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public SalonStaffDashboard() {
        setTitle("SalonCare System - Staff Dashboard");
        setSize(1200, 800); // Adjusted size for more content
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true); // Allow resizing for better layout management

        Color themeColor = new Color(128, 207, 192);
        Color lightGreen = new Color(224, 247, 245); // Lighter shade for background
        Color darkerGreen = new Color(100, 180, 170); // Darker shade for buttons

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        contentPane.setBackground(lightGreen); // Set overall background

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
            // Handle logout logic, e.g., show login frame
            new LoginFrame().setVisible(true);
            dispose();
        });
        topPanel.add(logoutButton, BorderLayout.EAST);

        // Main content panel using GridBagLayout for better structure
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
        newAppointmentPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for inner form
        newAppointmentPanel.setPreferredSize(new Dimension(400, 600)); // Fixed width for form

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(5, 5, 5, 5);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.weightx = 1.0;

        // Client Name
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        newAppointmentPanel.add(new JLabel("Client Name:"), formGbc);
        formGbc.gridy = 1;
        clientNameField = new JTextField(20);
        newAppointmentPanel.add(clientNameField, formGbc);

        // Contact Number
        formGbc.gridy = 2;
        newAppointmentPanel.add(new JLabel("Contact Number:"), formGbc);
        formGbc.gridy = 3;
        contactNumberField = new JTextField(20);
        newAppointmentPanel.add(contactNumberField, formGbc);

        // Type of Service
        formGbc.gridy = 4;
        newAppointmentPanel.add(new JLabel("Type of Service:"), formGbc);
        formGbc.gridy = 5;
        serviceComboBox = new JComboBox<>();
        loadServiceItems(); // Populate combo box
        newAppointmentPanel.add(serviceComboBox, formGbc);

        // Appointment Date
        formGbc.gridy = 6;
        newAppointmentPanel.add(new JLabel("Appointment Date:"), formGbc);
        formGbc.gridy = 7;
        dateField = new JFormattedTextField(DATE_INPUT_FORMATTER);
        dateField.setValue(LocalDate.now()); // Set default to today
        dateField.setColumns(10);
        newAppointmentPanel.add(dateField, formGbc);

        // Time (Hour, Minute, AM/PM)
        formGbc.gridy = 8;
        newAppointmentPanel.add(new JLabel("Time:"), formGbc);
        formGbc.gridy = 9;
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // No gaps
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
        for (int i = 0; i < 60; i += 5) { // Intervals of 5 minutes
            minuteComboBox.addItem(String.format("%02d", i));
        }
        minuteComboBox.setSelectedItem(String.format("%02d", (LocalTime.now().getMinute() / 5) * 5)); // Snap to nearest
                                                                                                      // 5 min
        timePanel.add(minuteComboBox);
        ampmComboBox = new JComboBox<>(new String[] { "AM", "PM" });
        ampmComboBox.setSelectedItem(LocalTime.now().getHour() >= 12 ? "PM" : "AM");
        timePanel.add(ampmComboBox);
        newAppointmentPanel.add(timePanel, formGbc);

        // Add Appointment Button
        formGbc.gridy = 10;
        formGbc.ipady = 10; // Add padding
        formGbc.insets = new Insets(20, 5, 5, 5); // Top padding
        addAppointmentButton = new JButton("ADD APPOINTMENT");
        addAppointmentButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addAppointmentButton.setBackground(darkerGreen);
        addAppointmentButton.setForeground(Color.WHITE);
        addAppointmentButton.setFocusPainted(false);
        newAppointmentPanel.add(addAppointmentButton, formGbc);

        // Update Existing Button
        formGbc.gridy = 11;
        formGbc.insets = new Insets(5, 5, 20, 5); // Bottom padding
        updateExistingButton = new JButton("UPDATE EXISTING");
        updateExistingButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateExistingButton.setBackground(themeColor.darker()); // Slightly darker theme color
        updateExistingButton.setForeground(Color.WHITE);
        updateExistingButton.setFocusPainted(false);
        updateExistingButton.setEnabled(false); // Disabled until an appointment is selected for edit
        newAppointmentPanel.add(updateExistingButton, formGbc);

        // Add newAppointmentPanel to mainContentPanel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3; // Take 30% of width
        gbc.weighty = 1.0;
        mainContentPanel.add(newAppointmentPanel, gbc);

        // --- Right Panel: Scheduled Appointments and Pending Requests ---
        JPanel rightSidePanel = new JPanel(new GridBagLayout());
        rightSidePanel.setBackground(lightGreen);

        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.insets = new Insets(0, 0, 10, 0); // No top inset for the search bar

        // Search Bar for Scheduled Appointments
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

        // // searchPanel.add(new JLabel(new
        // // 
        // // 
        // // n(getClass().getResource("/images/search_icon.png"))),
        // // BorderLayout.WEST); // Assuming you have a search icon
        // // searchPanel.add(searchClientField, BorderLayout.CENTER);
        // // searchPanel.add(searchButton, BorderLayout.EAST);
        // // searchPanel.setBorder(new EmptyBorder(5, 5, 5, 5)); // Padding for search bar

        // rightGbc.gridx = 0;
        // rightGbc.gridy = 0;
        // rightGbc.weightx = .0;
        // rightGbc.fill = GriBagConstraints.HORIZONTAL;
        // rightSidePanel.add(rightGbc);

        // Scheduled Appointments Scroll Pane
        scheduledAppointmentsPanel = new JPanel();
        scheduledAppointmentsPanel.setLayout(new BoxLayout(scheduledAppointmentsPanel, BoxLayout.Y_AXIS));
        scheduledAppointmentsPanel.setBackground(Color.WHITE);
        JScrollPane scheduledScrollPane = new JScrollPane(scheduledAppointmentsPanel);
        scheduledScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scheduledScrollPane.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(themeColor, 2), "SCHEDULED APPOINTMENTS", 0, 0,
                new Font("Segoe UI", Font.BOLD, 16), themeColor));
        scheduledScrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling

        rightGbc.gridx = 0;
        rightGbc.gridy = 1;
        rightGbc.weighty = 0.6; // Take 60% of vertical space
        rightGbc.fill = GridBagConstraints.BOTH;
        rightSidePanel.add(scheduledScrollPane, rightGbc);

        // Pending Requests Panel
        pendingRequestsContainer = new JPanel();
        pendingRequestsContainer.setLayout(new BoxLayout(pendingRequestsContainer, BoxLayout.Y_AXIS));
        pendingRequestsContainer.setBackground(Color.WHITE);

        JScrollPane pendingScrollPane = new JScrollPane(pendingRequestsContainer);
        pendingScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pendingScrollPane.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(themeColor, 2), "PENDING CLIENT REQUESTS", 0, 0,
                new Font("Segoe UI", Font.BOLD, 16), themeColor));
        pendingScrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling

        rightGbc.gridx = 0;
        rightGbc.gridy = 2;
        rightGbc.weighty = 0.4; // Take 40% of vertical space
        rightGbc.fill = GridBagConstraints.BOTH;
        rightSidePanel.add(pendingScrollPane, rightGbc);

        // Add rightSidePanel to mainContentPanel
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7; // Take 70% of width
        gbc.weighty = 1.0;
        mainContentPanel.add(rightSidePanel, gbc);

        // Initial load of appointments and requests
        refreshScheduledAppointments(null);
        refreshPendingRequests();
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

    // --- Core Logic for Appointments ---
    private void addAppointment(ActionEvent e) {
        String clientName = clientNameField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();
        String serviceName = (String) serviceComboBox.getSelectedItem();
        String dateStr = dateField.getText().trim();
        String hourStr = (String) hourComboBox.getSelectedItem();
        String minuteStr = (String) minuteComboBox.getSelectedItem();
        String ampm = (String) ampmComboBox.getSelectedItem();

        if (clientName.isEmpty() || contactNumber.isEmpty() || serviceName == null || dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all appointment details.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Client client = dataManager.getClientByEmailOrPhone(contactNumber);
        if (client == null) {
            // If client doesn't exist, create a new one for this appointment
            client = new Client(UUID.randomUUID().toString(), clientName, contactNumber, "", "");
            dataManager.clients.add(client);
            // Optionally, prompt staff if they want to save full client details
        } else if (!client.getName().equalsIgnoreCase(clientName)) {
            // Warn if contact number matches existing client but name is different
            int response = JOptionPane.showConfirmDialog(this,
                    "Contact number matches an existing client (" + client.getName()
                            + "). Do you want to use the existing client's data or create a new client?",
                    "Client Conflict", JOptionPane.YES_NO_CANCEL_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                // Use existing client, update name if needed
                client.setName(clientName);
            } else if (response == JOptionPane.NO_OPTION) {
                // Create new client (generate new ID)
                client = new Client(UUID.randomUUID().toString(), clientName, contactNumber, "", "");
                dataManager.clients.add(client);
            } else {
                return; // Cancel operation
            }
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
                hour = 0; // 12 AM is 00:00
            }
            LocalTime time = LocalTime.of(hour, Integer.parseInt(minuteStr));
            LocalDate date = LocalDate.parse(dateStr, DATE_INPUT_FORMATTER);
            appointmentDateTime = LocalDateTime.of(date, time);
        } catch (DateTimeParseException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date or time format.", "Input Error",
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
        String dateStr = dateField.getText().trim();
        String hourStr = (String) hourComboBox.getSelectedItem();
        String minuteStr = (String) minuteComboBox.getSelectedItem();
        String ampm = (String) ampmComboBox.getSelectedItem();

        if (clientName.isEmpty() || contactNumber.isEmpty() || serviceName == null || dateStr.isEmpty()) {
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
            // If contact matches a different client, ask for clarification
            int response = JOptionPane.showConfirmDialog(this,
                    "Contact number matches a different existing client (" + client.getName()
                            + "). Do you want to change this appointment's client to the matching one, or update the current client's contact?",
                    "Client Conflict", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                selectedAppointmentForEdit.setClient(client);
            } else if (response == JOptionPane.NO_OPTION) {
                // Update the original client's contact number or create new if truly distinct
                Client originalClient = selectedAppointmentForEdit.getClient();
                originalClient.setName(clientName); // Update name in original client
                originalClient.setPhone(contactNumber); // Update phone in original client
                client = originalClient; // Use original client with updated details
            } else {
                return; // Cancel update operation
            }
        } else {
            // Update selected appointment's client details if they changed
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
                hour = 0; // 12 AM is 00:00
            }
            LocalTime time = LocalTime.of(hour, Integer.parseInt(minuteStr));
            LocalDate date = LocalDate.parse(dateStr, DATE_INPUT_FORMATTER);
            appointmentDateTime = LocalDateTime.of(date, time);
        } catch (DateTimeParseException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date or time format.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check for conflicts with OTHER appointments (excluding the one being edited)
        if (!selectedAppointmentForEdit.getDateTime().equals(appointmentDateTime)
                || !selectedAppointmentForEdit.getClient().getId().equals(client.getId())) {
            // Only check for conflict if date/time or client has changed
            // Iterate through the raw list to find conflicts excluding the current
            // appointment
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

        // Update the fields of the selected appointment
        selectedAppointmentForEdit.setClient(client); // Set potentially new or updated client
        selectedAppointmentForEdit.setService(service);
        selectedAppointmentForEdit.setDateTime(appointmentDateTime);

        // Re-add to ensure sorting is maintained if date/time changed
        dataManager.appointments.removeById(selectedAppointmentForEdit.getId());
        dataManager.appointments.addSorted(selectedAppointmentForEdit);

        JOptionPane.showMessageDialog(this, "Appointment updated successfully!", "Success",
                JOptionPane.INFORMATION_MESSAGE);
        clearForm();
        refreshScheduledAppointments(null);
        selectedAppointmentForEdit = null; // Clear selection after update
        updateExistingButton.setEnabled(false); // Disable update button
        addAppointmentButton.setEnabled(true); // Re-enable add button
    }

    // --- UI Update Methods ---
    private void refreshScheduledAppointments(String searchText) {
        scheduledAppointmentsPanel.removeAll(); // Clear existing cards
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
            noAppointmentsLabel.setBorder(new EmptyBorder(50, 0, 50, 0)); // Add some padding
            scheduledAppointmentsPanel.add(noAppointmentsLabel);
        } else {
            for (int i = 0; i < currentAppointments.size(); i++) {
                Appointment appointment = currentAppointments.get(i);
                scheduledAppointmentsPanel.add(createAppointmentCard(appointment));
                if (i < currentAppointments.size() - 1) { // Add arrow between cards
                    scheduledAppointmentsPanel.add(createArrowLabel());
                }
            }
        }
        scheduledAppointmentsPanel.revalidate();
        scheduledAppointmentsPanel.repaint();
    }

    private void refreshPendingRequests() {
        pendingRequestsContainer.removeAll(); // Clear existing requests

        List<PendingAppointmentRequest> requests = dataManager.pendingRequests;
        // Sort requests by preferred date/time for better readability
        requests.sort(Comparator.comparing(PendingAppointmentRequest::getPreferredDateTime));

        if (requests.isEmpty()) {
            JLabel noRequestsLabel = new JLabel("No pending requests.", SwingConstants.CENTER);
            noRequestsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            noRequestsLabel.setForeground(Color.GRAY);
            noRequestsLabel.setBorder(new EmptyBorder(50, 0, 50, 0));
            pendingRequestsContainer.add(noRequestsLabel);
        } else {
            for (PendingAppointmentRequest request : requests) {
                pendingRequestsContainer.add(createPendingRequestCard(request));
            }
        }
        pendingRequestsContainer.revalidate();
        pendingRequestsContainer.repaint();
    }

    private JPanel createAppointmentCard(Appointment appointment) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(new LineBorder(new Color(192, 192, 192), 1, true));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120)); // Fixed height for cards

        // Display details
        JPanel detailsPanel = new JPanel(new GridLayout(4, 1));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(new EmptyBorder(5, 10, 5, 5));
        detailsPanel.add(new JLabel("Name: " + appointment.getClientName()));
        detailsPanel.add(new JLabel("Service: " + appointment.getServiceName()));
        detailsPanel.add(new JLabel("Date: " + appointment.getDateTime().format(DATE_TIME_FORMATTER).split(" ")[0] + " "
                + appointment.getDateTime().format(DATE_TIME_FORMATTER).split(" ")[1])); // Date part
        detailsPanel.add(new JLabel("Time: " + appointment.getDateTime().format(DATE_TIME_FORMATTER).split(" ")[2] + " "
                + appointment.getDateTime().format(DATE_TIME_FORMATTER).split(" ")[3])); // Time part

        card.add(detailsPanel, BorderLayout.CENTER);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonPanel.setBackground(Color.WHITE);
        JButton editButton = new JButton("EDIT");
        editButton.setBackground(new Color(60, 179, 113)); // Medium Sea Green
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> populateFormForEdit(appointment));

        JButton cancelButton = new JButton("CANCEL");
        cancelButton.setBackground(new Color(220, 20, 60)); // Crimson Red
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> confirmCancelAppointment(appointment));

        buttonPanel.add(editButton);
        buttonPanel.add(cancelButton);
        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }

    private JPanel createPendingRequestCard(PendingAppointmentRequest request) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(new LineBorder(new Color(255, 165, 0), 1, true)); // Orange border for pending
        card.setBackground(new Color(255, 250, 240)); // Light yellowish background
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Details
        JPanel detailsPanel = new JPanel(new GridLayout(4, 1));
        detailsPanel.setBackground(card.getBackground());
        detailsPanel.setBorder(new EmptyBorder(5, 10, 5, 5));
        detailsPanel.add(
                new JLabel("Client: " + request.getClient().getName() + " (" + request.getClient().getPhone() + ")"));
        detailsPanel.add(new JLabel("Service: " + request.getService().getName()));
        detailsPanel.add(new JLabel("Preferred: " + request.getPreferredDateTime().format(DATE_TIME_FORMATTER)));
        detailsPanel.add(new JLabel("Msg: " + (request.getClientMessage().isEmpty() ? "N/A"
                : request.getClientMessage().substring(0, Math.min(request.getClientMessage().length(), 40))
                        + (request.getClientMessage().length() > 40 ? "..." : "")))); // Truncate long messages

        card.add(detailsPanel, BorderLayout.CENTER);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonPanel.setBackground(card.getBackground());
        JButton approveButton = new JButton("Approve");
        approveButton.setBackground(new Color(34, 139, 34)); // Forest Green
        approveButton.setForeground(Color.WHITE);
        approveButton.setFocusPainted(false);
        approveButton.addActionListener(e -> approveRequest(request));

        JButton rejectButton = new JButton("Reject");
        rejectButton.setBackground(new Color(178, 34, 34)); // Firebrick Red
        rejectButton.setForeground(Color.WHITE);
        rejectButton.setFocusPainted(false);
        rejectButton.addActionListener(e -> rejectRequest(request));

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }

    private JLabel createArrowLabel() {
        // You might want a custom arrow image instead of text
        JLabel arrowLabel = new JLabel("<html>&darr;</html>", SwingConstants.CENTER); // Down arrow
        arrowLabel.setFont(new Font("Arial", Font.BOLD, 24));
        arrowLabel.setForeground(new Color(150, 150, 150));
        arrowLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the arrow horizontally
        arrowLabel.setBorder(new EmptyBorder(0, 0, 0, 0)); // Remove default padding
        return arrowLabel;
    }

    private void populateFormForEdit(Appointment appointment) {
        selectedAppointmentForEdit = appointment;
        clientNameField.setText(appointment.getClientName());
        contactNumberField.setText(appointment.getClientContact());
        serviceComboBox.setSelectedItem(appointment.getServiceName());

        dateField.setValue(appointment.getDateTime().toLocalDate());

        LocalTime time = appointment.getDateTime().toLocalTime();
        int hour = time.getHour();
        String ampm = "AM";
        if (hour >= 12) {
            ampm = "PM";
            if (hour > 12)
                hour -= 12;
        }
        if (hour == 0)
            hour = 12; // 00:xx becomes 12:xx AM

        hourComboBox.setSelectedItem(String.format("%02d", hour));
        minuteComboBox.setSelectedItem(String.format("%02d", (time.getMinute() / 5) * 5)); // Snap to nearest 5 min
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

    private void approveRequest(PendingAppointmentRequest request) {
        // Staff can optionally modify details before approving.
        // For simplicity, we'll directly convert it to an Appointment.

        // Check for conflicts before approving
        if (dataManager.appointments.hasConflict(request.getClient(), request.getPreferredDateTime())) {
            JOptionPane.showMessageDialog(this,
                    "Client " + request.getClient().getName() + " already has an appointment at "
                            + request.getPreferredDateTime().format(DATE_TIME_FORMATTER)
                            + ". Please choose another time or resolve the conflict manually.",
                    "Scheduling Conflict", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Appointment newAppointment = new Appointment(
                UUID.randomUUID().toString(), // Generate new ID for the actual appointment
                request.getClient(),
                request.getService(),
                request.getPreferredDateTime());
        dataManager.appointments.addSorted(newAppointment);
        dataManager.removePendingRequest(request.getRequestId()); // Remove from pending list

        JOptionPane.showMessageDialog(this,
                "Appointment for " + request.getClient().getName() + " approved and added to schedule.",
                "Request Approved", JOptionPane.INFORMATION_MESSAGE);
        refreshScheduledAppointments(null);
        refreshPendingRequests();
    }

    private void rejectRequest(PendingAppointmentRequest request) {
        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reject the request from " + request.getClient().getName() + " for "
                        + request.getService().getName() + " on "
                        + request.getPreferredDateTime().format(DATE_TIME_FORMATTER) + "?",
                "Confirm Rejection", JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            dataManager.removePendingRequest(request.getRequestId());
            JOptionPane.showMessageDialog(this, "Request from " + request.getClient().getName() + " rejected.",
                    "Request Rejected", JOptionPane.INFORMATION_MESSAGE);
            refreshPendingRequests();
        }
    }

    private void clearForm() {
        clientNameField.setText("");
        contactNumberField.setText("");
        serviceComboBox.setSelectedIndex(0);
        dateField.setValue(LocalDate.now());
        hourComboBox.setSelectedItem(
                String.format("%02d", LocalTime.now().getHour() % 12 == 0 ? 12 : LocalTime.now().getHour() % 12));
        minuteComboBox.setSelectedItem(String.format("%02d", (LocalTime.now().getMinute() / 5) * 5));
        ampmComboBox.setSelectedItem(LocalTime.now().getHour() >= 12 ? "PM" : "AM");

        selectedAppointmentForEdit = null;
        updateExistingButton.setEnabled(false);
        addAppointmentButton.setEnabled(true);
    }

    public static void main(String[] args) {
        // Initialize DataManager with some seed data
        DataManager.getInstance(); // Ensure data is seeded before UI loads
        SwingUtilities.invokeLater(() -> new SalonStaffDashboard().setVisible(true));
    }
}