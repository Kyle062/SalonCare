package gui;

import models.*;
import storage.DataManager;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class AppointmentsPanel extends JPanel {

    // Colors & fonts
    private final Color BG_PANEL = new Color(207, 236, 232);
    private final Color LEFT_CARD_BG = new Color(226, 243, 240);
    private final Color WHITE = Color.WHITE;
    private final Color PRIMARY = new Color(34, 150, 150);
    private final Color BTN_BLUE = new Color(59, 153, 160);
    private final Color BLUE_CARD = new Color(178, 231, 243);
    private final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private final Color BORDER = new Color(200, 210, 210);
    private final Color ARROW_COLOR = new Color(100, 100, 100);
    private final Color DANGER_RED = new Color(220, 53, 69);

    private final Font H1 = new Font("Segoe UI", Font.BOLD, 28);
    private final Font H2 = new Font("Segoe UI", Font.BOLD, 20);
    private final Font BODY = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    // Data
    private DataManager dataManager;
    private Client currentClient;
    private List<Appointment> appointmentList = new LinkedList<>();
    private List<Appointment> displayedList = new LinkedList<>();
    private Appointment selectedAppointment = null;

    // UI components
    private JTextField searchField;
    private JPanel appointmentsListPanel;
    private JPanel timelinePanel;
    private JTextField nameField, contactField, emailField;
    private JComboBox<String> serviceCombo;
    private JComboBox<String> hourCombo, minuteCombo, ampmCombo;
    private JFormattedTextField dateField;
    private JButton addAppointmentBtn, editBtn, cancelBtn;
    private JLabel statusLabel;

    // Arrow panel for connecting appointments
    private JPanel arrowsPanel;

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public AppointmentsPanel(int width, int height, Client client) {
        this.currentClient = client;
        this.dataManager = DataManager.getInstance();

        setLayout(null);
        setBackground(BG_PANEL);
        setPreferredSize(new Dimension(width, height));
        setBounds(0, 0, width, height);

        createComponents();
        loadClientAppointments();
        refreshAppointments();
    }

    private void createComponents() {
        createTopBar();
        createLeftFormPanel();
        createRightPanel();
        createTimelinePanel();
    }

    private void createTopBar() {
        // Centered search field
        JPanel searchWrapper = new RoundedPanel(30, WHITE);
        searchWrapper.setBounds(250, 30, 520, 52);
        searchWrapper.setLayout(new BorderLayout(10, 0));

        searchField = new JTextField();
        searchField.setFont(BODY);
        searchField.setBorder(null);
        searchField.setOpaque(false);
        searchField.putClientProperty("JTextField.placeholderText", "Search appointments by service or name...");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchAppointment();
            }
        });

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        searchIcon.setBorder(new EmptyBorder(0, 15, 0, 10));

        searchWrapper.add(searchField, BorderLayout.CENTER);
        searchWrapper.add(searchIcon, BorderLayout.WEST);
        add(searchWrapper);

        // Top-right action buttons
        editBtn = createRoundedBlueButton("Edit Appointment");
        editBtn.setBounds(800, 35, 180, 40);
        editBtn.addActionListener(e -> editSelectedAppointment());
        editBtn.setEnabled(false);
        add(editBtn);

        cancelBtn = createRoundedRedButton("Cancel Appointment");
        cancelBtn.setBounds(1000, 35, 180, 40);
        cancelBtn.addActionListener(e -> deleteSelectedAppointment());
        cancelBtn.setEnabled(false);
        add(cancelBtn);

        // Status label for messages
        statusLabel = new JLabel("Ready");
        statusLabel.setBounds(250, 85, 500, 25);
        statusLabel.setFont(BODY);
        statusLabel.setForeground(new Color(80, 80, 80));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(statusLabel);
    }

    private void createLeftFormPanel() {
        // Left form panel container
        JPanel leftPanel = new JPanel();
        leftPanel.setBounds(40, 120, 450, 580);
        leftPanel.setBackground(LEFT_CARD_BG);
        leftPanel.setBorder(new CompoundBorder(new LineBorder(BORDER, 1), new EmptyBorder(20, 24, 20, 24)));
        leftPanel.setLayout(null);
        add(leftPanel);

        // Title with plus icon
        JLabel plus = new JLabel("➕");
        plus.setBounds(20, 15, 40, 40);
        plus.setFont(new Font("Segoe UI", Font.PLAIN, 34));
        leftPanel.add(plus);

        JLabel title = new JLabel("New Appointment");
        title.setBounds(70, 20, 250, 30);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        leftPanel.add(title);

        // Form fields
        int y = 80;
        int labelWidth = 150;
        int fieldWidth = 200;
        int fieldHeight = 32;

        // Fullname
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setBounds(20, y, labelWidth, 24);
        nameLabel.setFont(BODY_BOLD);
        leftPanel.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(180, y, fieldWidth, fieldHeight);
        nameField.setFont(BODY);
        nameField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1), new EmptyBorder(6, 10, 6, 10)));
        nameField.setEditable(false);
        leftPanel.add(nameField);
        y += 45;

        // Contact
        JLabel contactLabel = new JLabel("Phone Number:");
        contactLabel.setBounds(20, y, labelWidth, 24);
        contactLabel.setFont(BODY_BOLD);
        leftPanel.add(contactLabel);

        contactField = new JTextField();
        contactField.setBounds(180, y, fieldWidth, fieldHeight);
        contactField.setFont(BODY);
        contactField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1), new EmptyBorder(6, 10, 6, 10)));
        contactField.setEditable(false);
        leftPanel.add(contactField);
        y += 45;

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(20, y, labelWidth, 24);
        emailLabel.setFont(BODY_BOLD);
        leftPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(180, y, fieldWidth, fieldHeight);
        emailField.setFont(BODY);
        emailField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1), new EmptyBorder(6, 10, 6, 10)));
        emailField.setEditable(false);
        leftPanel.add(emailField);
        y += 45;

        // Service Dropdown
        JLabel serviceLabel = new JLabel("Service:*");
        serviceLabel.setBounds(20, y, labelWidth, 24);
        serviceLabel.setFont(BODY_BOLD);
        leftPanel.add(serviceLabel);

        serviceCombo = new JComboBox<>();
        // Get services from DataManager
        List<ServiceItem> services = dataManager.getServicesList();
        for (ServiceItem service : services) {
            serviceCombo.addItem(service.getName());
        }
        serviceCombo.setBounds(180, y, fieldWidth, fieldHeight);
        serviceCombo.setFont(BODY);
        leftPanel.add(serviceCombo);
        y += 45;

        // Date Field (formatted)
        JLabel dateLabel = new JLabel("Date (MM/DD/YYYY):*");
        dateLabel.setBounds(20, y, labelWidth, 24);
        dateLabel.setFont(BODY_BOLD);
        leftPanel.add(dateLabel);

        dateField = new JFormattedTextField(new java.text.SimpleDateFormat("MM/dd/yyyy"));
        dateField.setValue(java.sql.Date.valueOf(LocalDate.now()));
        dateField.setBounds(180, y, fieldWidth, fieldHeight);
        dateField.setFont(BODY);
        dateField.setBorder(new CompoundBorder(new LineBorder(BORDER, 1), new EmptyBorder(6, 10, 6, 10)));
        leftPanel.add(dateField);
        y += 45;

        // Time Selection
        JLabel timeLabel = new JLabel("Time:*");
        timeLabel.setBounds(20, y, labelWidth, 24);
        timeLabel.setFont(BODY_BOLD);
        leftPanel.add(timeLabel);

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        timePanel.setBounds(180, y, fieldWidth, fieldHeight);
        timePanel.setOpaque(false);

        hourCombo = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            hourCombo.addItem(String.format("%02d", i));
        }
        hourCombo.setSelectedItem(String.format("%02d",
                LocalTime.now().getHour() % 12 == 0 ? 12 : LocalTime.now().getHour() % 12));
        hourCombo.setFont(BODY);
        timePanel.add(hourCombo);

        timePanel.add(new JLabel(":"));

        minuteCombo = new JComboBox<>();
        for (int i = 0; i < 60; i += 5) {
            minuteCombo.addItem(String.format("%02d", i));
        }
        minuteCombo.setSelectedItem(String.format("%02d", (LocalTime.now().getMinute() / 5) * 5));
        minuteCombo.setFont(BODY);
        timePanel.add(minuteCombo);

        ampmCombo = new JComboBox<>(new String[] { "AM", "PM" });
        ampmCombo.setSelectedItem(LocalTime.now().getHour() >= 12 ? "PM" : "AM");
        ampmCombo.setFont(BODY);
        timePanel.add(ampmCombo);

        leftPanel.add(timePanel);
        y += 60;

        // Add Appointment button
        addAppointmentBtn = createRoundedBlueButton("Schedule Appointment");
        addAppointmentBtn.setBounds(120, y, 220, 40);
        addAppointmentBtn.addActionListener(e -> requestAppointment());
        leftPanel.add(addAppointmentBtn);

        // Pre-fill client info if available
        if (currentClient != null) {
            nameField.setText(currentClient.getName());
            contactField.setText(currentClient.getPhone());
            emailField.setText(currentClient.getEmail());
        }
    }

    private void createRightPanel() {
        // Scheduled Appointment title
        JLabel titleLabel = new JLabel("Scheduled Appointments");
        titleLabel.setBounds(520, 120, 300, 30);
        titleLabel.setFont(H2);
        titleLabel.setForeground(TEXT_PRIMARY);
        add(titleLabel);

        // Main appointments container with proper scroll pane
        JPanel rightContainer = new JPanel();
        rightContainer.setBounds(520, 160, 760, 300);
        rightContainer.setLayout(new BorderLayout());
        rightContainer.setOpaque(false);
        rightContainer.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        add(rightContainer);

        // Appointments list panel
        appointmentsListPanel = new JPanel();
        appointmentsListPanel.setLayout(new BoxLayout(appointmentsListPanel, BoxLayout.Y_AXIS));
        appointmentsListPanel.setOpaque(false);
        appointmentsListPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Scroll pane with proper configuration
        JScrollPane scrollPane = new JScrollPane(appointmentsListPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(new Color(240, 248, 247));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        rightContainer.add(scrollPane, BorderLayout.CENTER);

        // Arrows panel (positioned absolutely over the scroll pane)
        arrowsPanel = new JPanel(null);
        arrowsPanel.setBounds(520, 160, 760, 300);
        arrowsPanel.setOpaque(false);
        add(arrowsPanel);
    }

    private void createTimelinePanel() {
        JPanel timelineContainer = new JPanel(new BorderLayout());
        timelineContainer.setBounds(520, 480, 760, 180);
        timelineContainer.setOpaque(false);
        timelineContainer.setBorder(new EmptyBorder(10, 0, 10, 0));
        add(timelineContainer);

        JLabel timelineTitle = new JLabel("Upcoming Timeline");
        timelineTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timelineTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        timelineContainer.add(timelineTitle, BorderLayout.NORTH);

        timelinePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        timelinePanel.setOpaque(false);
        timelinePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane timelineScroll = new JScrollPane(timelinePanel);
        timelineScroll.setBorder(null);
        timelineScroll.setOpaque(false);
        timelineScroll.getViewport().setOpaque(false);
        timelineScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        timelineScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        timelineContainer.add(timelineScroll, BorderLayout.CENTER);
    }

    private JButton createRoundedBlueButton(String text) {
        JButton b = new JButton(text);
        b.setFont(BODY_BOLD);
        b.setBackground(BTN_BLUE);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(BTN_BLUE.darker());
            }

            public void mouseExited(MouseEvent e) {
                b.setBackground(BTN_BLUE);
            }
        });
        return b;
    }

    private JButton createRoundedRedButton(String text) {
        JButton b = new JButton(text);
        b.setFont(BODY_BOLD);
        b.setBackground(DANGER_RED);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(DANGER_RED.darker());
            }

            public void mouseExited(MouseEvent e) {
                b.setBackground(DANGER_RED);
            }
        });
        return b;
    }

    private void loadClientAppointments() {
        appointmentList.clear();
        if (currentClient == null)
            return;
        List<Appointment> clientAppointments = dataManager.getClientAppointments(currentClient.getEmail());
        if (clientAppointments != null) {
            appointmentList.addAll(clientAppointments);
        }
        displayedList = new LinkedList<>(appointmentList);
        statusLabel.setText("Loaded " + appointmentList.size() + " appointment(s)");
    }

    private void refreshAppointments() {
        appointmentsListPanel.removeAll();
        arrowsPanel.removeAll();
        timelinePanel.removeAll();

        // Sort appointments by date/time
        displayedList.sort((a, b) -> a.getDateTime().compareTo(b.getDateTime()));

        if (displayedList.isEmpty()) {
            JLabel none = new JLabel("No scheduled appointments found");
            none.setFont(BODY);
            none.setForeground(TEXT_PRIMARY);
            none.setAlignmentX(Component.CENTER_ALIGNMENT);
            none.setBorder(new EmptyBorder(50, 0, 0, 0));
            appointmentsListPanel.add(none);
        } else {
            for (int i = 0; i < displayedList.size(); i++) {
                Appointment appt = displayedList.get(i);
                JPanel card = createAppointmentCard(appt);
                appointmentsListPanel.add(card);
                appointmentsListPanel.add(Box.createRigidArea(new Dimension(0, 10)));

                // Add arrow after each card except the last one
                if (i < displayedList.size() - 1) {
                    int arrowY = 10 + i * 80;
                    JLabel arrow = new JLabel("↓");
                    arrow.setBounds(370, arrowY, 20, 20);
                    arrow.setFont(new Font("Segoe UI", Font.BOLD, 20));
                    arrow.setForeground(ARROW_COLOR);
                    arrow.setHorizontalAlignment(SwingConstants.CENTER);
                    arrowsPanel.add(arrow);
                }
            }
        }

        // Build timeline
        buildTimeline();

        // Update UI
        appointmentsListPanel.revalidate();
        appointmentsListPanel.repaint();
        arrowsPanel.revalidate();
        arrowsPanel.repaint();
        timelinePanel.revalidate();
        timelinePanel.repaint();

        // Update button states
        editBtn.setEnabled(selectedAppointment != null);
        cancelBtn.setEnabled(selectedAppointment != null);
    }

    private JPanel createAppointmentCard(Appointment appt) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(appt.equals(selectedAppointment) ? PRIMARY : new Color(180, 180, 180),
                        appt.equals(selectedAppointment) ? 2 : 1),
                new EmptyBorder(10, 15, 10, 15)));
        card.setMaximumSize(new Dimension(730, 70));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Time section
        JLabel timeLabel = new JLabel(appt.getDateTime().format(DateTimeFormatter.ofPattern("h:mm a")));
        timeLabel.setFont(BODY_BOLD);
        timeLabel.setPreferredSize(new Dimension(100, 30));
        card.add(timeLabel, BorderLayout.WEST);

        // Center section with name and date
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(appt.getClient().getName());
        nameLabel.setFont(BODY);

        JLabel dateLabel = new JLabel(appt.getDateTime().format(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy")));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(100, 100, 100));

        centerPanel.add(nameLabel, BorderLayout.CENTER);
        centerPanel.add(dateLabel, BorderLayout.SOUTH);
        card.add(centerPanel, BorderLayout.CENTER);

        // Right section with service and status
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        JLabel serviceLabel = new JLabel(appt.getService().getName());
        serviceLabel.setFont(BODY_BOLD);
        serviceLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel statusLabel = new JLabel("✓ Confirmed");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(40, 167, 69));
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        rightPanel.add(serviceLabel, BorderLayout.CENTER);
        rightPanel.add(statusLabel, BorderLayout.SOUTH);
        card.add(rightPanel, BorderLayout.EAST);

        // Click handler
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                selectedAppointment = appt;
                populateFormWithAppointment(appt);
                refreshAppointments();
            }
        });

        return card;
    }

    private void buildTimeline() {
        List<Appointment> upcoming = new LinkedList<>(displayedList);
        upcoming.sort((a, b) -> a.getDateTime().compareTo(b.getDateTime()));
        int count = Math.min(3, upcoming.size());

        for (int i = 0; i < count; i++) {
            Appointment a = upcoming.get(i);
            JPanel box = createTimelineBox(a);
            timelinePanel.add(box);

            if (i < count - 1) {
                JLabel arrow = new JLabel("→");
                arrow.setFont(new Font("Segoe UI", Font.BOLD, 24));
                arrow.setForeground(ARROW_COLOR);
                timelinePanel.add(arrow);
            }
        }
    }

    private JPanel createTimelineBox(Appointment a) {
        JPanel box = new JPanel(new BorderLayout());
        box.setPreferredSize(new Dimension(140, 100));
        box.setBackground(BLUE_CARD);
        box.setBorder(new CompoundBorder(
                new LineBorder(BLUE_CARD.darker(), 1),
                new EmptyBorder(10, 10, 10, 10)));

        JLabel timeLabel = new JLabel(a.getDateTime().format(DateTimeFormatter.ofPattern("h:mm a")));
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        box.add(timeLabel, BorderLayout.NORTH);

        JLabel nameLabel = new JLabel("<html><center>" + a.getClient().getName() + "</center></html>");
        nameLabel.setFont(BODY);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        box.add(nameLabel, BorderLayout.CENTER);

        JLabel serviceLabel = new JLabel("<html><center>" + a.getService().getName() + "</center></html>");
        serviceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        serviceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        box.add(serviceLabel, BorderLayout.SOUTH);

        return box;
    }

    private void populateFormWithAppointment(Appointment appt) {
        if (appt == null)
            return;

        nameField.setText(appt.getClient().getName());
        contactField.setText(appt.getClient().getPhone());
        emailField.setText(appt.getClient().getEmail());
        serviceCombo.setSelectedItem(appt.getService().getName());

        dateField.setValue(java.sql.Date.valueOf(appt.getDateTime().toLocalDate()));

        LocalTime time = appt.getDateTime().toLocalTime();
        int hour = time.getHour();
        String ampm = "AM";
        if (hour >= 12) {
            ampm = "PM";
            if (hour > 12)
                hour -= 12;
        }
        if (hour == 0)
            hour = 12;

        hourCombo.setSelectedItem(String.format("%02d", hour));
        minuteCombo.setSelectedItem(String.format("%02d", (time.getMinute() / 5) * 5));
        ampmCombo.setSelectedItem(ampm);

        statusLabel.setText("Selected: " + appt.getService().getName() + " at " +
                appt.getDateTime().format(DateTimeFormatter.ofPattern("h:mm a, MMM d")));
    }

    private void clearForm() {
        serviceCombo.setSelectedIndex(0);
        dateField.setValue(java.sql.Date.valueOf(LocalDate.now()));
        hourCombo.setSelectedItem(String.format("%02d",
                LocalTime.now().getHour() % 12 == 0 ? 12 : LocalTime.now().getHour() % 12));
        minuteCombo.setSelectedItem(String.format("%02d", (LocalTime.now().getMinute() / 5) * 5));
        ampmCombo.setSelectedItem(LocalTime.now().getHour() >= 12 ? "PM" : "AM");

        statusLabel.setText("Ready");
        selectedAppointment = null;
        editBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
    }

    private void requestAppointment() {
        try {
            String serviceName = (String) serviceCombo.getSelectedItem();
            if (serviceName == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select a service",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Parse date and time
            java.util.Date dateValue = (java.util.Date) dateField.getValue();
            if (dateValue == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select a valid date",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate date = dateValue.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

            String hourStr = (String) hourCombo.getSelectedItem();
            String minuteStr = (String) minuteCombo.getSelectedItem();
            String ampm = (String) ampmCombo.getSelectedItem();

            int hour = Integer.parseInt(hourStr);
            if (ampm.equals("PM") && hour != 12) {
                hour += 12;
            } else if (ampm.equals("AM") && hour == 12) {
                hour = 0;
            }

            LocalTime time = LocalTime.of(hour, Integer.parseInt(minuteStr));
            LocalDateTime dt = LocalDateTime.of(date, time);

            // Validate date/time
            if (dt.isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this,
                        "Cannot schedule appointment in the past",
                        "Invalid Time",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (time.isBefore(LocalTime.of(8, 0)) || time.isAfter(LocalTime.of(20, 0))) {
                JOptionPane.showMessageDialog(this,
                        "Business hours are 8:00 AM to 8:00 PM",
                        "Outside Business Hours",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check for time slot availability
            if (isTimeSlotBooked(dt)) {
                JOptionPane.showMessageDialog(this,
                        "This time slot is already booked. Please choose another time.",
                        "Time Not Available",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get or create service
            ServiceItem service = dataManager.getServiceByName(serviceName);
            if (service == null) {
                service = new ServiceItem(serviceName, 0.0);
                dataManager.services.add(service);
            }

            // Create and add appointment
            Appointment appt = new Appointment(UUID.randomUUID().toString(), currentClient, service, dt);
            appt.setConfirmed(true); // Directly confirmed (no pending requests)
            dataManager.appointments.addSorted(appt);

            loadClientAppointments();
            refreshAppointments();
            clearForm();

            JOptionPane.showMessageDialog(this,
                    "Appointment scheduled successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "An error occurred: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private boolean isTimeSlotBooked(LocalDateTime dt) {
        for (Appointment existing : dataManager.getAppointmentsList()) {
            if (existing.getDateTime().equals(dt)) {
                return true;
            }
        }
        return false;
    }

    private void deleteSelectedAppointment() {
        if (selectedAppointment == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select an appointment to cancel first",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ask for cancellation reason
        String reason = JOptionPane.showInputDialog(this,
                "Please provide a reason for cancellation (optional):",
                "Cancellation Reason",
                JOptionPane.QUESTION_MESSAGE);

        if (reason == null)
            return; // User cancelled

        // Create cancellation request
        CancellationRequest cancellation = new CancellationRequest(
                UUID.randomUUID().toString(),
                selectedAppointment.getId(),
                currentClient,
                selectedAppointment.getService(),
                selectedAppointment.getDateTime(),
                reason.isEmpty() ? "No reason provided" : reason);

        dataManager.addCancellationRequest(cancellation);

        selectedAppointment = null;
        loadClientAppointments();
        refreshAppointments();
        clearForm();

        JOptionPane.showMessageDialog(this,
                "Cancellation request submitted. Please wait for staff confirmation.",
                "Cancellation Requested",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void editSelectedAppointment() {
        if (selectedAppointment == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select an appointment to edit first",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String serviceName = (String) serviceCombo.getSelectedItem();
            if (serviceName == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select a service",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Parse date and time
            java.util.Date dateValue = (java.util.Date) dateField.getValue();
            if (dateValue == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select a valid date",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate date = dateValue.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

            String hourStr = (String) hourCombo.getSelectedItem();
            String minuteStr = (String) minuteCombo.getSelectedItem();
            String ampm = (String) ampmCombo.getSelectedItem();

            int hour = Integer.parseInt(hourStr);
            if (ampm.equals("PM") && hour != 12) {
                hour += 12;
            } else if (ampm.equals("AM") && hour == 12) {
                hour = 0;
            }

            LocalTime time = LocalTime.of(hour, Integer.parseInt(minuteStr));
            LocalDateTime newDateTime = LocalDateTime.of(date, time);

            // Validate date/time
            if (newDateTime.isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this,
                        "Cannot schedule appointment in the past",
                        "Invalid Time",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (time.isBefore(LocalTime.of(8, 0)) || time.isAfter(LocalTime.of(20, 0))) {
                JOptionPane.showMessageDialog(this,
                        "Business hours are 8:00 AM to 8:00 PM",
                        "Outside Business Hours",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check for time slot availability (excluding the appointment being edited)
            for (Appointment existing : dataManager.getAppointmentsList()) {
                if (!existing.getId().equals(selectedAppointment.getId()) &&
                        existing.getDateTime().equals(newDateTime)) {
                    JOptionPane.showMessageDialog(this,
                            "This time slot is already booked. Please choose another time.",
                            "Time Not Available",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // Get service
            ServiceItem service = dataManager.getServiceByName(serviceName);
            if (service == null) {
                service = new ServiceItem(serviceName, 0.0);
                dataManager.services.add(service);
            }

            // Update appointment
            selectedAppointment.setService(service);
            selectedAppointment.setDateTime(newDateTime);

            loadClientAppointments();
            refreshAppointments();

            JOptionPane.showMessageDialog(this,
                    "Appointment updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "An error occurred: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void searchAppointment() {
        String query = searchField.getText().trim().toLowerCase();

        if (query.isEmpty()) {
            displayedList = new LinkedList<>(appointmentList);
        } else {
            displayedList = new LinkedList<>();
            for (Appointment appt : appointmentList) {
                if (appt.getService().getName().toLowerCase().contains(query) ||
                        appt.getClient().getName().toLowerCase().contains(query) ||
                        appt.getDateTime().toString().toLowerCase().contains(query)) {
                    displayedList.add(appt);
                }
            }
        }

        refreshAppointments();
        statusLabel.setText("Found " + displayedList.size() + " appointment(s) matching '" + query + "'");
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color background;

        RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.background = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(background);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(new Color(220, 220, 220));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}