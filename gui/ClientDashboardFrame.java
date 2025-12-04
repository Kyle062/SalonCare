package gui;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.*;

public class ClientDashboardFrame extends JFrame {

    // --- SERVICE CARD CLASS ---
    private class ServiceCard extends JPanel {
        final int CARD_PREF_WIDTH = 300;
        final int CARD_PREF_HEIGHT = 380;
        final int IMAGE_HEIGHT = 220;
        private String serviceNameText;
        private int ratingGridX = 1;
        private int ratingGridY = 2;
        private Insets ratingInsets = new Insets(0, 6, 15, 15);

        public ServiceCard(String serviceName, String price, String imageUrl, Color themeColor, int imageWidth,
                int imageHeight) {
            this.serviceNameText = serviceName;

            setPreferredSize(new Dimension(CARD_PREF_WIDTH, CARD_PREF_HEIGHT));
            setLayout(new GridBagLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 0, 0, 0);

            // 1. Service Image
            JLabel serviceImage = new JLabel();
            ImageIcon cardImage = scaleImage(imageUrl, CARD_PREF_WIDTH + imageWidth, IMAGE_HEIGHT + imageHeight);

            if (cardImage != null) {
                serviceImage.setIcon(cardImage);
            } else {
                serviceImage.setText("No Image");
                serviceImage.setHorizontalAlignment(SwingConstants.CENTER);
                serviceImage.setOpaque(true);
                serviceImage.setBackground(new Color(240, 240, 240));
                serviceImage.setPreferredSize(new Dimension(CARD_PREF_WIDTH, IMAGE_HEIGHT));
            }
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            add(serviceImage, gbc);

            // 2. Service Name
            JLabel nameLabel = new JLabel(serviceName);
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
            JLabel priceLabel = new JLabel("₱" + price);
            priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            priceLabel.setForeground(Color.GRAY.darker());
            priceLabel.setHorizontalAlignment(SwingConstants.LEFT);
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 1;
            gbc.insets = new Insets(0, 15, 15, 5);
            add(priceLabel, gbc);

            // Rating panel (stars + rating number)
            JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            ratingPanel.setBackground(Color.WHITE);

            JLabel starsLabel = new JLabel("★★★★★");
            starsLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));
            starsLabel.setForeground(new Color(255, 200, 0));

            JLabel ratingText = new JLabel("5.0");
            ratingText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            ratingText.setForeground(Color.GRAY);

            ratingPanel.add(starsLabel);
            ratingPanel.add(ratingText);

            gbc.gridx = ratingGridX;
            gbc.gridy = ratingGridY;
            gbc.insets = ratingInsets;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.EAST;
            add(ratingPanel, gbc);

            // Hover effect
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBorder(BorderFactory.createLineBorder(themeColor, 2));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
                }
            });
        }

        public String getServiceName() {
            return serviceNameText;
        }
    }

    private JPanel mainContentPanel;
    private JScrollPane scrollPane;
    private JLabel noResultsLabel;
    private JButton servicesBtn;
    private JButton myAppointmentsBtn;
    private JPanel rightJPanel;

    // UI Components that need to be accessible
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

    public ClientDashboardFrame() {
        // Frame Setup
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        setTitle("SalonCare Client Dashboard");
        setBackground(new Color(245, 245, 245));

        setVisible(true);

        final int FRAME_WIDTH = getWidth();
        final int FRAME_HEIGHT = getHeight();

        // --- PROPORTIONAL DIMENSIONS ---
        final int LEFT_PANEL_WIDTH = (int) (FRAME_WIDTH * 0.18);
        final int RIGHT_PANEL_WIDTH = FRAME_WIDTH - LEFT_PANEL_WIDTH;
        final int TOP_BAR_HEIGHT = (int) (FRAME_HEIGHT * 0.12);

        // --- LEFT PANEL (Navigation/Sidebar) ---
        JPanel leftJPanel = new JPanel();
        leftJPanel.setBounds(0, 0, LEFT_PANEL_WIDTH, FRAME_HEIGHT);
        leftJPanel.setBackground(lightMintTeal);
        leftJPanel.setLayout(null);

        // Logo/Brand area
        JPanel logoArea = new JPanel();
        logoArea.setBounds((LEFT_PANEL_WIDTH / 2) - 60, 40, 120, 120);
        logoArea.setBackground(mintTeal);
        logoArea.setLayout(new GridBagLayout());
        logoArea.setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));
        logoArea.setOpaque(false);

        JLabel logoIcon = new JLabel();
        ImageIcon logoImg = scaleImage("images/logo_placeholder.png", 80, 80);

        if (logoImg != null) {
            logoIcon.setIcon(logoImg);
        } else {
            logoIcon.setText("SC");
            logoIcon.setFont(new Font("Arial", Font.BOLD, 40));
            logoIcon.setForeground(Color.WHITE);
        }
        logoArea.add(logoIcon);
        leftJPanel.add(logoArea);

        JLabel logoText = new JLabel("The Salon Care");
        logoText.setFont(new Font("Times New Roman", Font.ITALIC | Font.BOLD, 28));
        logoText.setForeground(darkGrayText);
        logoText.setHorizontalAlignment(SwingConstants.CENTER);
        logoText.setBounds(0, logoArea.getY() + logoArea.getHeight() + 10, LEFT_PANEL_WIDTH, 40);
        leftJPanel.add(logoText);

        // Navigation Buttons
        int buttonYStart = (int) (FRAME_HEIGHT * 0.35);
        int buttonWidth = (int) (LEFT_PANEL_WIDTH * 0.7);
        int buttonHeight = (int) (FRAME_HEIGHT * 0.05);
        int buttonX = (LEFT_PANEL_WIDTH - buttonWidth) / 2;

        servicesBtn = new JButton("View Services");
        servicesBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        servicesBtn.setBounds(buttonX, buttonYStart, buttonWidth, buttonHeight);
        servicesBtn.setBackground(mintTeal);
        servicesBtn.setForeground(Color.WHITE);
        servicesBtn.setFocusPainted(false);
        servicesBtn.setBorderPainted(false);
        servicesBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leftJPanel.add(servicesBtn);

        myAppointmentsBtn = new JButton("My Appointments");
        myAppointmentsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        myAppointmentsBtn.setBounds(buttonX, buttonYStart + buttonHeight + 15, buttonWidth, buttonHeight);
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
        logoutBtn.setBounds(buttonX, FRAME_HEIGHT - (int) (FRAME_HEIGHT * 0.15), buttonWidth, buttonHeight);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leftJPanel.add(logoutBtn);

        add(leftJPanel);

        // --- RIGHT PANEL (Main Content Area) ---
        rightJPanel = new JPanel();
        rightJPanel.setBounds(LEFT_PANEL_WIDTH, 0, RIGHT_PANEL_WIDTH, FRAME_HEIGHT);
        rightJPanel.setBackground(Color.WHITE);
        rightJPanel.setLayout(null);
        add(rightJPanel);

        // Initialize services view components
        initializeServicesView(RIGHT_PANEL_WIDTH, TOP_BAR_HEIGHT);

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
                showAppointmentsView(RIGHT_PANEL_WIDTH, FRAME_HEIGHT);
            }
        });

        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private void initializeServicesView(int rightPanelWidth, int topBarHeight) {
        // "Our Services" Title
        servicesTitle = new JLabel("Our Services");
        servicesTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        servicesTitle.setForeground(darkGrayText);
        servicesTitle.setBounds((int) (rightPanelWidth * 0.04), (int) (topBarHeight * 0.3),
                (int) (rightPanelWidth * 0.3), (int) (topBarHeight * 0.4));

        // Search Bar
        int searchWidth = (int) (rightPanelWidth * 0.25);
        int searchHeight = (int) (topBarHeight * 0.4);
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setForeground(Color.GRAY.darker());
        searchField.setBounds(rightPanelWidth - searchWidth - (int) (rightPanelWidth * 0.1),
                (int) (topBarHeight * 0.35), searchWidth, searchHeight);
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
        searchBtn.setBounds(searchField.getX() + searchField.getWidth() + 10,
                searchField.getY(), 100, searchHeight);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color normalBg = mintTeal;
        Color hoverBg = new Color(
                Math.max(0, mintTeal.getRed() - 10),
                Math.max(0, mintTeal.getGreen() - 12),
                Math.max(0, mintTeal.getBlue() - 10));

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

        // Profile Icon
        profileIcon = new JLabel();
        ImageIcon profileImg = scaleImage("images/profile_placeholder.png", 40, 40);

        if (profileImg != null) {
            profileIcon.setIcon(profileImg);
        } else {
            profileIcon.setText("👤");
            profileIcon.setFont(new Font("Arial", Font.BOLD, 25));
            profileIcon.setForeground(Color.GRAY);
        }
        profileIcon.setBounds(rightPanelWidth - (int) (rightPanelWidth * 0.05) - 40,
                (int) (topBarHeight * 0.35), 40, 40);
        profileIcon.setHorizontalAlignment(SwingConstants.CENTER);

        // Separator Line
        separator = new JPanel();
        separator.setBackground(lightGrayBorder);
        separator.setBounds(0, topBarHeight, rightPanelWidth, 1);

        // --- Scrollable Services Grid Area ---
        mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new GridLayout(0, 3, 40, 40));
        mainContentPanel.setBackground(Color.WHITE);
        mainContentPanel.setBorder(
                new EmptyBorder(40, (int) (rightPanelWidth * 0.04), 40, (int) (rightPanelWidth * 0.04)));

        // Add Service Cards
        mainContentPanel.add(new ServiceCard("Signature Haircut", "800.00", "images/haircut.png", mintTeal, 155, 160));
        mainContentPanel.add(
                new ServiceCard("Deep Cleansing Facial", "1,500.00", "images/service_facial.jpg", mintTeal, 155, 80));
        mainContentPanel.add(new ServiceCard("Full Balayage Color", "3,200.00", "images/service_hair_color.jpg",
                mintTeal, 155, 100));
        mainContentPanel
                .add(new ServiceCard("Classic Manicure", "950.00", "images/service_manicure.jpg", mintTeal, 155, 100));
        mainContentPanel
                .add(new ServiceCard("Eyebrow Threading", "550.00", "images/service_eyebrow.jpg", mintTeal, 155, 100));
        mainContentPanel.add(
                new ServiceCard("Relaxing Body Massage", "1,800.00", "images/service_massage.jpg", mintTeal, 155, 100));
        mainContentPanel.add(new ServiceCard("Permanent Hair Straight", "4,500.00", "images/service_straight.jpg",
                mintTeal, 100, 100));
        mainContentPanel
                .add(new ServiceCard("Basic Facial", "700.00", "images/service_basic_facial.jpg", mintTeal, 155, 100));
        mainContentPanel.add(
                new ServiceCard("Hot Stone Massage", "2,500.00", "images/service_hot_stone.jpg", mintTeal, 155, 100));

        // Scroll Pane for the main content
        scrollPane = new JScrollPane(mainContentPanel);
        scrollPane.setBounds(0, topBarHeight + 1, rightPanelWidth, getHeight() - topBarHeight - 1);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // "No results" label
        noResultsLabel = new JLabel("No services found", SwingConstants.CENTER);
        noResultsLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        noResultsLabel.setForeground(Color.GRAY);
        noResultsLabel.setBounds(0, topBarHeight + 1, rightPanelWidth, getHeight() - topBarHeight - 1);
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
                    String name = card.getServiceName();
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

    private void showAppointmentsView(int width, int height) {
        currentView = ViewState.APPOINTMENTS;

        // Clear right panel
        rightJPanel.removeAll();
        rightJPanel.setLayout(new BorderLayout());

        // Create and add appointments panel
        AppointmentsPanel appointmentsPanel = new AppointmentsPanel(width, height);
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
        javax.swing.SwingUtilities.invokeLater(() -> {
            new ClientDashboardFrame();
        });
    }

    private ImageIcon scaleImage(String path, int width, int height) {
        URL imgUrl = ClientDashboardFrame.class.getClassLoader().getResource(path);
        if (imgUrl != null) {
            ImageIcon originalIcon = new ImageIcon(imgUrl);
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        }
        return null;
    }
}