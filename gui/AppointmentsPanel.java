package gui;

import models.*;
import storage.DataManager;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class AppointmentsPanel extends JPanel {

    // Modern color palette
    private final Color PRIMARY = new Color(128, 207, 192); // Mint Teal
    private final Color PRIMARY_DARK = new Color(110, 187, 172);
    private final Color DANGER = new Color(220, 80, 80);
    private final Color SUCCESS = new Color(40, 167, 69);
    private final Color WARNING = new Color(255, 193, 7);

    private final Color BG_LIGHT = Color.WHITE; // Changed to white to match dashboard
    private final Color CARD_WHITE = Color.WHITE;
    private final Color BORDER = new Color(222, 226, 230);
    private final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private final Color HOVER = new Color(245, 247, 250);

    // Modern fonts
    private final Font FONT_H1 = new Font("Segoe UI", Font.BOLD, 36); // Matching dashboard
    private final Font FONT_H2 = new Font("Segoe UI", Font.BOLD, 20);
    private final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    private final Font FONT_BTN = new Font("Segoe UI Semibold", Font.PLAIN, 14);

    // Data structures
    private List<Appointment> appointmentList = new LinkedList<>();
    private List<Appointment> displayedList = new LinkedList<>();
    private Appointment selectedAppointment = null;
    private Client currentClient;
    private DataManager dataManager;

    // UI components
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JTextField nameField, contactField, serviceField, dateField, timeField;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final int PANEL_WIDTH;
    private final int PANEL_HEIGHT;

    public AppointmentsPanel(int width, int height, Client client) {
        this.currentClient = client;
        this.dataManager = DataManager.getInstance();
        this.PANEL_WIDTH = width;
        this.PANEL_HEIGHT = height;
        setLayout(new BorderLayout());
        setBackground(BG_LIGHT);

        // Create main panel with proper dimensions
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_LIGHT);
        mainPanel.setPreferredSize(new Dimension(width, height));

        // Header panel with proper positioning
        JPanel headerPanel = createHeaderPanel(width);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center content with proper spacing
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(BG_LIGHT);
        centerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Table panel - using exact dimensions
        JPanel tablePanel = createTablePanel(width);
        centerPanel.add(tablePanel, BorderLayout.NORTH);

        // Form panel - using exact dimensions
        JPanel formPanel = createFormPanel(width);
        centerPanel.add(formPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Add to main panel
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        // Load appointments from DataManager
        loadClientAppointments();
        displayedList = new LinkedList<>(appointmentList);
        refreshTable();

        // Table selection listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = table.getSelectedRow();
                if (r >= 0 && r < displayedList.size()) {
                    selectedAppointment = displayedList.get(r);
                    populateFormWithAppointment(selectedAppointment);
                }
            }
        });
    }

    private void loadClientAppointments() {
        appointmentList.clear();

        // Get appointments for this client from DataManager
        List<Appointment> clientAppointments = dataManager.getClientAppointments(currentClient.getEmail());
        appointmentList.addAll(clientAppointments);
        displayedList = new LinkedList<>(appointmentList);
    }

    private JPanel createHeaderPanel(int width) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_LIGHT);
        panel.setBorder(new EmptyBorder(20, 30, 0, 30));
        panel.setPreferredSize(new Dimension(width, 80));

        // Title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(BG_LIGHT);

        JLabel iconLabel = new JLabel("📅");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 15));

        JLabel titleLabel = new JLabel("My Appointments");
        titleLabel.setFont(FONT_H1);
        titleLabel.setForeground(TEXT_PRIMARY);

        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);

        // Search panel - fixed width
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(BG_LIGHT);
        searchPanel.setPreferredSize(new Dimension(400, 50));

        // Modern search field with icon
        JPanel searchContainer = new JPanel(new BorderLayout());
        searchContainer.setBackground(CARD_WHITE);
        searchContainer.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(0, 15, 0, 0)));

        searchField = new JTextField();
        searchField.setFont(FONT_BODY);
        searchField.setBorder(null);
        searchField.setBackground(CARD_WHITE);
        searchField.setForeground(TEXT_PRIMARY);
        searchField.putClientProperty("JTextField.placeholderText", "Search appointments by name...");

        // Search icon
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchIcon.setBorder(new EmptyBorder(0, 0, 0, 15));
        searchIcon.setForeground(TEXT_SECONDARY);

        searchContainer.add(searchField, BorderLayout.CENTER);
        searchContainer.add(searchIcon, BorderLayout.EAST);

        // Search button with modern styling
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(FONT_BTN);
        searchBtn.setBackground(PRIMARY);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.setBorder(new EmptyBorder(10, 24, 10, 24));
        searchBtn.setPreferredSize(new Dimension(100, 50));
        searchBtn.addActionListener(e -> searchAppointment());

        // Add hover effect
        searchBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                searchBtn.setBackground(PRIMARY_DARK);
            }

            public void mouseExited(MouseEvent e) {
                searchBtn.setBackground(PRIMARY);
            }
        });

        // Add Enter key listener
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchAppointment();
                }
            }
        });

        JPanel searchWrapper = new JPanel(new BorderLayout(10, 0));
        searchWrapper.setBackground(BG_LIGHT);
        searchWrapper.add(searchContainer, BorderLayout.CENTER);
        searchWrapper.add(searchBtn, BorderLayout.EAST);

        searchPanel.add(searchWrapper, BorderLayout.CENTER);

        panel.add(titlePanel, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createTablePanel(int width) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_LIGHT);
        panel.setBorder(new EmptyBorder(0, 30, 0, 30));
        panel.setPreferredSize(new Dimension(width, 320));

        // Table header with stats
        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(CARD_WHITE);
        tableHeader.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(15, 20, 15, 20)));

        JLabel tableTitle = new JLabel("Appointments List");
        tableTitle.setFont(FONT_H2);
        tableTitle.setForeground(TEXT_PRIMARY);

        JLabel statsLabel = new JLabel("Total: " + appointmentList.size() + " appointments");
        statsLabel.setFont(FONT_SMALL);
        statsLabel.setForeground(TEXT_SECONDARY);

        tableHeader.add(tableTitle, BorderLayout.WEST);
        tableHeader.add(statsLabel, BorderLayout.EAST);

        // Table setup with fixed height
        String[] cols = { "Client Name", "Phone", "Service", "Date & Time", "Status" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        styleModernTable(table);

        // Set preferred column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(150); // Name
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Phone
        table.getColumnModel().getColumn(2).setPreferredWidth(180); // Service
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Date & Time
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Status

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(width - 60, 200));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 1, 1, 1, BORDER),
                BorderFactory.createEmptyBorder()));
        scrollPane.getViewport().setBackground(CARD_WHITE);

        panel.add(tableHeader, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFormPanel(int width) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_LIGHT);
        panel.setBorder(new EmptyBorder(20, 30, 30, 30));

        // Form header
        JPanel formHeader = new JPanel(new BorderLayout());
        formHeader.setBackground(CARD_WHITE);
        formHeader.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(15, 20, 15, 20)));

        JLabel formTitle = new JLabel("Appointment Details");
        formTitle.setFont(FONT_H2);
        formTitle.setForeground(TEXT_PRIMARY);

        JLabel formSubtitle = new JLabel("Fill in the details to request an appointment");
        formSubtitle.setFont(FONT_SMALL);
        formSubtitle.setForeground(TEXT_SECONDARY);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setBackground(CARD_WHITE);
        titlePanel.add(formTitle);
        titlePanel.add(formSubtitle);

        formHeader.add(titlePanel, BorderLayout.WEST);

        // Main form content
        JPanel formContent = new JPanel(new GridBagLayout());
        formContent.setBackground(CARD_WHITE);
        formContent.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 1, 1, 1, BORDER),
                new EmptyBorder(30, 30, 30, 30)));
        formContent.setPreferredSize(new Dimension(width - 60, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Name and Phone - Pre-filled with client info
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        addFormLabel(formContent, "Client Name *", gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        nameField = createModernTextField(currentClient.getName());
        nameField.setEditable(false); // Can't change name
        formContent.add(nameField, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        addFormLabel(formContent, "Phone Number *", gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        contactField = createModernTextField(currentClient.getPhone());
        contactField.setEditable(false); // Can't change phone
        formContent.add(contactField, gbc);

        // Row 2: Service and Date
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        addFormLabel(formContent, "Service *", gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        serviceField = createModernTextField("Select or enter service name");
        formContent.add(serviceField, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        addFormLabel(formContent, "Date *", gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        dateField = createModernTextField("YYYY-MM-DD (e.g., 2024-12-25)");
        formContent.add(dateField, gbc);

        // Row 3: Time
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        addFormLabel(formContent, "Time *", gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        timeField = createModernTextField("HH:MM (24h format, e.g., 14:30)");
        formContent.add(timeField, gbc);

        // Action buttons - properly centered
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(CARD_WHITE);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton clearBtn = createSecondaryButton("Clear Form");
        clearBtn.addActionListener(e -> clearForm());

        JButton cancelBtn = createDangerButton("Cancel Appointment");
        cancelBtn.addActionListener(e -> deleteAppointment());

        JButton bookBtn = createSuccessButton("Request Appointment");
        bookBtn.addActionListener(e -> addAppointment());

        buttonPanel.add(clearBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(bookBtn);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        formContent.add(buttonPanel, gbc);

        panel.add(formHeader, BorderLayout.NORTH);
        panel.add(formContent, BorderLayout.CENTER);

        return panel;
    }

    private void styleModernTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(48);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 30));
        table.setSelectionForeground(TEXT_PRIMARY);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BODY_BOLD);
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 2, 0, PRIMARY),
                new EmptyBorder(12, 16, 12, 16)));
        header.setReorderingAllowed(false);

        // Cell padding
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                label.setBorder(new EmptyBorder(12, 16, 12, 16));
                label.setForeground(TEXT_PRIMARY);
                return label;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 4) { // Skip status column
                table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
            }
        }

        // Status column special styling with modern badge design
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(CENTER);
                label.setOpaque(false);

                String status = value != null ? value.toString().toUpperCase() : "PENDING";
                Color bgColor;
                Color textColor = Color.WHITE;

                switch (status) {
                    case "CONFIRMED":
                        bgColor = SUCCESS;
                        break;
                    case "CANCELLED":
                        bgColor = DANGER;
                        break;
                    case "COMPLETED":
                        bgColor = TEXT_SECONDARY;
                        break;
                    default:
                        bgColor = WARNING;
                        textColor = TEXT_PRIMARY;
                }

                // Create a modern badge
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(bgColor.darker(), 1),
                        new EmptyBorder(6, 20, 6, 20)));
                label.setBackground(bgColor);
                label.setForeground(textColor);
                label.setOpaque(true);

                return label;
            }
        });
    }

    private JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(new Color(248, 249, 250));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(12, 15, 12, 15)));
        field.putClientProperty("JTextField.placeholderText", placeholder);

        // Add hover effect
        field.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!field.hasFocus()) {
                    field.setBackground(HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!field.hasFocus()) {
                    field.setBackground(new Color(248, 249, 250));
                }
            }
        });

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBackground(Color.WHITE);
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(PRIMARY, 2),
                        new EmptyBorder(11, 14, 11, 14)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBackground(new Color(248, 249, 250));
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER, 1),
                        new EmptyBorder(12, 15, 12, 15)));
            }
        });

        return field;
    }

    private void addFormLabel(JPanel parent, String text, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BODY_BOLD);
        label.setForeground(TEXT_PRIMARY);
        parent.add(label, gbc);
    }

    private JButton createSuccessButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BTN);
        button.setBackground(SUCCESS);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(12, 30, 12, 30));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(33, 136, 56));
                button.setBorder(new EmptyBorder(12, 30, 12, 30));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(SUCCESS);
            }
        });

        return button;
    }

    private JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BTN);
        button.setBackground(DANGER);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(12, 30, 12, 30));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(200, 35, 51));
                button.setBorder(new EmptyBorder(12, 30, 12, 30));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(DANGER);
            }
        });

        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BTN);
        button.setBackground(new Color(108, 117, 125));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(12, 30, 12, 30));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(90, 98, 104));
                button.setBorder(new EmptyBorder(12, 30, 12, 30));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(108, 117, 125));
            }
        });

        return button;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Appointment appt : displayedList) {
            String status = appt.isConfirmed() ? "Confirmed" : "Pending";
            tableModel.addRow(new Object[] {
                    appt.getClient().getName(),
                    appt.getClient().getPhone(),
                    appt.getService().getName(),
                    appt.getDateTime().format(formatter),
                    status
            });
        }
    }

    private void populateFormWithAppointment(Appointment appt) {
        if (appt == null)
            return;

        nameField.setText(appt.getClient().getName());
        contactField.setText(appt.getClient().getPhone());
        serviceField.setText(appt.getService().getName());
        dateField.setText(appt.getDateTime().toLocalDate().toString());
        timeField.setText(appt.getDateTime().toLocalTime().toString());
    }

    private void clearForm() {
        nameField.setText(currentClient.getName());
        contactField.setText(currentClient.getPhone());
        serviceField.setText("");
        dateField.setText("");
        timeField.setText("");

        selectedAppointment = null;
        table.clearSelection();
    }

    private void addAppointment() {
        try {
            String serviceName = serviceField.getText().trim();
            String date = dateField.getText().trim();
            String time = timeField.getText().trim();

            if (serviceName.isEmpty() || date.isEmpty() || time.isEmpty()) {
                showStyledDialog("Warning", "Please fill all required fields (*)",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!validateDateTime(date, time)) {
                return;
            }

            LocalDateTime dt = LocalDateTime.parse(date + " " + time, formatter);

            // Get service from DataManager
            ServiceItem service = dataManager.getServiceByName(serviceName);
            if (service == null) {
                // Create new service if not found
                service = new ServiceItem(serviceName, 0.0);
                dataManager.services.add(service);
            }

            // Create appointment - NOT CONFIRMED (pending staff approval)
            Appointment a = new Appointment(
                    UUID.randomUUID().toString(),
                    currentClient,
                    service,
                    dt);
            a.setConfirmed(false); // Start as pending

            // Add to DataManager
            dataManager.appointments.addSorted(a);

            // Refresh local list
            loadClientAppointments();
            refreshTable();
            clearForm();

            showStyledDialog("Success",
                    "Appointment request submitted!\n" +
                            "Date: " + dt.toLocalDate() + "\n" +
                            "Time: " + dt.toLocalTime() + "\n" +
                            "Service: " + serviceName + "\n\n" +
                            "Status: Pending - Waiting for staff confirmation.",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            showStyledDialog("Error",
                    "Invalid date/time format!<br>Date: YYYY-MM-DD (e.g., 2024-12-25)<br>Time: HH:mm (e.g., 14:30)",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAppointment() {
        if (selectedAppointment == null) {
            showStyledDialog("Warning", "Please select an appointment to cancel",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><div style='width:300px;padding:10px;'>" +
                        "<h3 style='margin-top:0;color:#dc3545;'>Confirm Cancellation</h3>" +
                        "<p>Are you sure you want to cancel this appointment?</p>" +
                        "<div style='background:#f8f9fa;padding:10px;border-radius:5px;margin:10px 0;'>" +
                        "<b>Client:</b> " + selectedAppointment.getClient().getName() + "<br>" +
                        "<b>Service:</b> " + selectedAppointment.getService().getName() + "<br>" +
                        "<b>Time:</b> " + selectedAppointment.getDateTime().format(formatter) + "</div>" +
                        "<p><i>This action cannot be undone.</i></p>" +
                        "</div></html>",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Remove from DataManager
            dataManager.appointments.removeById(selectedAppointment.getId());

            // Refresh local list
            loadClientAppointments();
            refreshTable();
            clearForm();

            showStyledDialog("Success", "Appointment cancelled successfully!",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void searchAppointment() {
        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            displayedList = new LinkedList<>(appointmentList);
            refreshTable();
            return;
        }

        List<Appointment> results = new LinkedList<>();
        for (Appointment a : appointmentList) {
            if (a.getClient().getName().toLowerCase().contains(query.toLowerCase()) ||
                    a.getService().getName().toLowerCase().contains(query.toLowerCase())) {
                results.add(a);
            }
        }

        displayedList = results;
        refreshTable();

        if (results.isEmpty() && !query.isEmpty()) {
            showStyledDialog("Information", "No appointments found for: " + query,
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean validateDateTime(String dateStr, String timeStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            LocalTime time = LocalTime.parse(timeStr);

            if (date.isBefore(LocalDate.now())) {
                showStyledDialog("Warning", "Date cannot be in the past!",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if (time.isBefore(LocalTime.of(8, 0)) || time.isAfter(LocalTime.of(20, 0))) {
                showStyledDialog("Warning", "Time must be between 08:00 and 20:00",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }

            return true;

        } catch (Exception e) {
            showStyledDialog("Error", "Invalid date/time format!<br>Date: YYYY-MM-DD<br>Time: HH:mm",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void showStyledDialog(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='width:300px;padding:10px;font-family:Segoe UI;'>" +
                        message.replace("\n", "<br>") +
                        "</div></html>",
                title,
                messageType);
    }
}