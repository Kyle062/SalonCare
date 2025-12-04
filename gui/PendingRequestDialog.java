package gui;

import models.*;
import storage.DataManager;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class PendingRequestDialog extends JDialog {
    private Client currentClient;
    private ServiceItem selectedService;
    private JTextArea messageArea;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    public PendingRequestDialog(JFrame parent, Client client, ServiceItem service) {
        super(parent, "Book " + service.getName(), true);
        this.currentClient = client;
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
        JLabel titleLabel = new JLabel("Book: " + selectedService.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(128, 207, 192));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Price label
        JLabel priceLabel = new JLabel("Price: ₱" + String.format("%.2f", selectedService.getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        headerPanel.add(priceLabel, BorderLayout.EAST);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Client Info (read-only)
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Client:"), gbc);

        gbc.gridx = 1;
        JTextField clientField = new JTextField(currentClient.getName() + " (" + currentClient.getPhone() + ")");
        clientField.setEditable(false);
        formPanel.add(clientField, gbc);

        // Date Selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Preferred Date:"), gbc);

        gbc.gridx = 1;
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "MM/dd/yyyy");
        dateSpinner.setEditor(dateEditor);
        formPanel.add(dateSpinner, gbc);

        // Time Selection
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Preferred Time:"), gbc);

        gbc.gridx = 1;
        timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "hh:mm a");
        timeSpinner.setEditor(timeEditor);
        formPanel.add(timeSpinner, gbc);

        // Message
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(new JLabel("Special Requests (optional):"), gbc);

        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        messageArea = new JTextArea(3, 20);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        formPanel.add(scrollPane, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton submitButton = new JButton("Submit Request");
        JButton cancelButton = new JButton("Cancel");

        submitButton.setBackground(new Color(128, 207, 192));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);

        cancelButton.setBackground(new Color(220, 80, 80));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

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
                    currentClient,
                    selectedService,
                    preferredDateTime,
                    message);

            // Add to DataManager
            DataManager.getInstance().addPendingRequest(request);

            JOptionPane.showMessageDialog(this,
                    "Appointment request submitted!\n\n" +
                            "Service: " + selectedService.getName() + "\n" +
                            "Date: " + preferredDateTime.format(DATE_FORMATTER) + "\n" +
                            "Time: " + preferredDateTime.format(TIME_FORMATTER) + "\n\n" +
                            "The salon staff will review your request and confirm your appointment.",
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