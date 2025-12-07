package gui;

import models.Client;
import models.ServiceItem;
import storage.DataManager;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ClientDashboardFrame extends JFrame {

    // --- SERVICE CARD CLASS ---
    private class ServiceCard extends JPanel {
        final int CARD_PREF_WIDTH = 300;
        final int CARD_PREF_HEIGHT = 380;
        final int IMAGE_HEIGHT = 220;
        private ServiceItem serviceItem;
        private String serviceNameText;

        public ServiceCard(ServiceItem serviceItem, Color themeColor) {
            this.serviceItem = serviceItem;
            this.serviceNameText = serviceItem.getName();

            setPreferredSize(new Dimension(CARD_PREF_WIDTH, CARD_PREF_HEIGHT));
            setLayout(new GridBagLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 0, 0, 0);

            // 1. Service Image
            JLabel serviceImage = new JLabel();
            serviceImage.setOpaque(true);
            serviceImage.setBackground(new Color(240, 240, 240));
            serviceImage.setPreferredSize(new Dimension(CARD_PREF_WIDTH, IMAGE_HEIGHT));
            serviceImage.setHorizontalAlignment(SwingConstants.CENTER);
            serviceImage.setText(serviceItem.getName());
            serviceImage.setFont(new Font("Segoe UI", Font.BOLD, 16));

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            add(serviceImage, gbc);

            // 2. Service Name
            JLabel nameLabel = new JLabel(serviceItem.getName());
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            nameLabel.setForeground(Color.BLACK);
            nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weighty = 0;
            gbc.insets = new Insets(15, 15, 5, 15);
            add(nameLabel, gbc);

            // 3. Price
            JLabel priceLabel = new JLabel("₱" + String.format("%.2f", serviceItem.getPrice()));
            priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            priceLabel.setForeground(Color.GRAY.darker());
            priceLabel.setHorizontalAlignment(SwingConstants.LEFT);
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(0, 15, 15, 15);
            add(priceLabel, gbc);
        }
    }

    private Client currentClient;
    private JPanel mainContentPanel;
    private JScrollPane scrollPane;
    private JLabel noResultsLabel;
    private JButton servicesBtn;
    private JButton myAppointmentsBtn;
    private JPanel rightJPanel;

    // UI Components
    private JLabel servicesTitle;
    private JTextField searchField;
    private JButton searchBtn;
    private JLabel profileIcon;
    private JPanel separator;

    // Colors
    private final Color mintTeal = new Color(128, 207, 192);
    private final Color lightMintTeal = new Color(220, 240, 235);
    private final Color softRed = new Color(220, 80, 80);
    private final Color darkGrayText = new Color(70, 70, 70);
    private final Color lightGrayBorder = new Color(220, 220, 220);

    // Current view state
    private enum ViewState {
        SERVICES, APPOINTMENTS
    }

    private ViewState currentView = ViewState.SERVICES;

    public ClientDashboardFrame(Client client) {
        this.currentClient = client;

        // Frame Setup
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        setTitle("SalonCare Client Dashboard - Welcome, " + client.getName() + "!");
        setBackground(new Color(245, 245, 245));

        setVisible(true);

        final int FRAME_WIDTH = getWidth();
        final int FRAME_HEIGHT = getHeight();

        // --- LEFT PANEL (Navigation/Sidebar) ---
        JPanel leftJPanel = new JPanel();
        leftJPanel.setBounds(0, 0, 345, FRAME_HEIGHT);
        leftJPanel.setBackground(lightMintTeal);
        leftJPanel.setLayout(null);

        // Logo/Brand area
        JPanel logoArea = new JPanel();
        logoArea.setBounds(112, 40, 120, 120);
        logoArea.setBackground(mintTeal);
        logoArea.setLayout(new GridBagLayout());
        logoArea.setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));
        logoArea.setOpaque(false);

        JLabel logoIcon = new JLabel("SC");
        logoIcon.setFont(new Font("Arial", Font.BOLD, 40));
        logoIcon.setForeground(Color.WHITE);
        logoArea.add(logoIcon);
        leftJPanel.add(logoArea);

        JLabel logoText = new JLabel("The Salon Care");
        logoText.setFont(new Font("Times New Roman", Font.ITALIC | Font.BOLD, 28));
        logoText.setForeground(darkGrayText);
        logoText.setHorizontalAlignment(SwingConstants.CENTER);
        logoText.setBounds(0, 170, 345, 40);
        leftJPanel.add(logoText);

        // Navigation Buttons
        servicesBtn = new JButton("Home");
        servicesBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        servicesBtn.setBounds(51, 430, 242, 50);
        servicesBtn.setBackground(mintTeal);
        servicesBtn.setForeground(Color.WHITE);
        servicesBtn.setFocusPainted(false);
        servicesBtn.setBorderPainted(false);
        servicesBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leftJPanel.add(servicesBtn);

        myAppointmentsBtn = new JButton("My Appointments");
        myAppointmentsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        myAppointmentsBtn.setBounds(51, 495, 242, 50);
        myAppointmentsBtn.setBackground(new Color(0, 0, 0, 0));
        myAppointmentsBtn.setForeground(darkGrayText);
        myAppointmentsBtn.setFocusPainted(false);
        myAppointmentsBtn.setBorderPainted(false);
        myAppointmentsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leftJPanel.add(myAppointmentsBtn);

        // Log Out Button
        JButton logoutBtn = new JButton("Log Out");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoutBtn.setBackground(softRed);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBounds(51, 900, 242, 50);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leftJPanel.add(logoutBtn);

        add(leftJPanel);

        // --- RIGHT PANEL (Main Content Area) ---
        rightJPanel = new JPanel();
        rightJPanel.setBounds(345, 0, 1595, FRAME_HEIGHT);
        rightJPanel.setBackground(Color.WHITE);
        rightJPanel.setLayout(null);
        add(rightJPanel);

        // Initialize services view components
        initializeServicesView();

        // Show initial services view
        showServicesView();

        // --- Action Listeners ---
        servicesBtn.addActionListener(e -> {
            if (currentView != ViewState.SERVICES) {
                showServicesView();
            }
        });

        myAppointmentsBtn.addActionListener(e -> {
            if (currentView != ViewState.APPOINTMENTS) {
                showAppointmentsView();
            }
        });

        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private void initializeServicesView() {
        // "Our Services" Title
        servicesTitle = new JLabel("Our Services");
        servicesTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        servicesTitle.setForeground(darkGrayText);
        servicesTitle.setBounds(63, 80, 478, 100);

        // Search Bar
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setForeground(Color.GRAY.darker());
        searchField.setBounds(1000, 100, 398, 50);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(lightGrayBorder, 1, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));

        // Search button
        searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchBtn.setBackground(mintTeal);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setOpaque(true);
        searchBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(lightGrayBorder, 1, true),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        searchBtn.setBounds(1400, 100, 100, 50);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color normalBg = mintTeal;
        Color hoverBg = new Color(118, 197, 182);

        searchBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                searchBtn.setBackground(hoverBg);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                searchBtn.setBackground(normalBg);
            }
        });

        // Profile Icon with client name
        profileIcon = new JLabel(currentClient.getName().substring(0, 1).toUpperCase());
        profileIcon.setFont(new Font("Arial", Font.BOLD, 20));
        profileIcon.setForeground(Color.WHITE);
        profileIcon.setBackground(mintTeal);
        profileIcon.setOpaque(true);
        profileIcon.setHorizontalAlignment(SwingConstants.CENTER);
        profileIcon.setBounds(1500, 100, 50, 50);
        profileIcon.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        // Separator Line
        separator = new JPanel();
        separator.setBackground(lightGrayBorder);
        separator.setBounds(0, 275, 1595, 1);

        // --- Scrollable Services Grid Area ---
        mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new GridLayout(0, 3, 40, 40));
        mainContentPanel.setBackground(Color.WHITE);
        mainContentPanel.setBorder(new EmptyBorder(40, 63, 40, 63));

        // Add Service Cards from DataManager
        DataManager dataManager = DataManager.getInstance();
        for (ServiceItem service : dataManager.services) {
            mainContentPanel.add(new ServiceCard(service, mintTeal));
        }

        // Scroll Pane for the main content
        scrollPane = new JScrollPane(mainContentPanel);
        scrollPane.setBounds(0, 276, 1595, 724);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // "No results" label
        noResultsLabel = new JLabel("No services found", SwingConstants.CENTER);
        noResultsLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        noResultsLabel.setForeground(Color.GRAY);
        noResultsLabel.setBounds(0, 276, 1595, 724);
        noResultsLabel.setVisible(false);

        // Search logic
        ActionListener performSearch = e -> {
            String query = searchField.getText();
            if (query == null)
                query = "";
            query = query.trim().toLowerCase();

            boolean anyVisible = false;
            for (java.awt.Component comp : mainContentPanel.getComponents()) {
                if (comp instanceof ServiceCard) {
                    ServiceCard card = (ServiceCard) comp;
                    String name = card.serviceNameText;
                    if (name != null && name.toLowerCase().contains(query)) {
                        card.setVisible(true);
                        anyVisible = true;
                    } else {
                        card.setVisible(false);
                    }
                }
            }
            if (query.isEmpty()) {
                for (java.awt.Component comp : mainContentPanel.getComponents()) {
                    comp.setVisible(true);
                }
                anyVisible = true;
            }

            scrollPane.setVisible(anyVisible);
            noResultsLabel.setVisible(!anyVisible);
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        };

        searchBtn.addActionListener(performSearch);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch.actionPerformed(null);
                }
            }
        });
    }

    private void showServicesView() {
        currentView = ViewState.SERVICES;

        // Clear right panel
        rightJPanel.removeAll();
        rightJPanel.setLayout(null);

        // Add services view components
        rightJPanel.add(servicesTitle);
        rightJPanel.add(searchField);
        rightJPanel.add(searchBtn);
        rightJPanel.add(profileIcon);
        rightJPanel.add(separator);
        rightJPanel.add(scrollPane);
        rightJPanel.add(noResultsLabel);

        // Update button states
        servicesBtn.setBackground(mintTeal);
        servicesBtn.setForeground(Color.WHITE);
        servicesBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));

        myAppointmentsBtn.setBackground(new Color(0, 0, 0, 0));
        myAppointmentsBtn.setForeground(darkGrayText);
        myAppointmentsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        rightJPanel.revalidate();
        rightJPanel.repaint();
    }

    private void showAppointmentsView() {
        currentView = ViewState.APPOINTMENTS;

        // Clear right panel
        rightJPanel.removeAll();
        rightJPanel.setLayout(new BorderLayout());

        // Create and add appointments panel
        AppointmentsPanel appointmentsPanel = new AppointmentsPanel(1595, 1000, currentClient);
        rightJPanel.add(appointmentsPanel, BorderLayout.CENTER);

        // Update button states
        servicesBtn.setBackground(new Color(0, 0, 0, 0));
        servicesBtn.setForeground(darkGrayText);
        servicesBtn.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        myAppointmentsBtn.setBackground(mintTeal);
        myAppointmentsBtn.setForeground(Color.WHITE);
        myAppointmentsBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));

        rightJPanel.revalidate();
        rightJPanel.repaint();
    }

    public static void main(String[] args) {
        // For testing only
        javax.swing.SwingUtilities.invokeLater(() -> {
            DataManager dm = DataManager.getInstance();
            Client testClient = dm.clients.get(0);
            new ClientDashboardFrame(testClient);
        });
    }
}