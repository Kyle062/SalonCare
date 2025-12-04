package gui;

import models.Client;
import storage.DataManager;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.net.URL;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        Color themeColor = new Color(128, 207, 192);

        setSize(1300, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        setTitle("Login Frame");

        // --- LEFT PANEL (Image) ---
        JPanel leftJPanel = new JPanel();
        leftJPanel.setBounds(0, 0, 650, 900);
        leftJPanel.setBackground(Color.WHITE);
        leftJPanel.setLayout(null);
        add(leftJPanel);

        // --- RIGHT PANEL (Login Form) ---
        JPanel rightJPanel = new JPanel();
        rightJPanel.setBounds(650, 0, 650, 900);
        rightJPanel.setBackground(Color.WHITE);
        rightJPanel.setLayout(null);
        add(rightJPanel);

        // --- Image Loading ---
        JLabel imageLabel = new JLabel();
        imageLabel.setBounds(0, 0, 650, 900);
        imageLabel.setLayout(null);

        JLabel logoLabel = new JLabel();
        logoLabel.setBounds(60, 100, 580, 580);

        JLabel logoLabel2 = new JLabel();
        logoLabel2.setBounds(140, 0, 400, 400);

        JLabel headingLabel = new JLabel(
                "<html>DYNAMIC CLIENT SCHEDULING SYSTEM <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;FOR BEAUTY SERVICES</html>");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 19));
        headingLabel.setBounds(150, 515, 580, 50);

        // Load images
        URL imageURL = LoginFrame.class.getClassLoader().getResource("images/backgroundLogo.jpg");
        URL imageLogoURL = LoginFrame.class.getClassLoader().getResource("images/LogoFinal1.png");
        URL imageLogoURL2 = LoginFrame.class.getClassLoader().getResource("images/LogoSalonCare2.png");

        if (imageURL != null) {
            ImageIcon originalIcon = new ImageIcon(imageURL);
            Image scaledImage = originalIcon.getImage().getScaledInstance(650, 900, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        }

        if (imageLogoURL != null) {
            ImageIcon originalLogo = new ImageIcon(imageLogoURL);
            Image scaledLogoImage = originalLogo.getImage().getScaledInstance(580, 580, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledLogoImage));
        }

        if (imageLogoURL2 != null) {
            ImageIcon originalLogo2 = new ImageIcon(imageLogoURL2);
            Image scaledLogoImage2 = originalLogo2.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
            logoLabel2.setIcon(new ImageIcon(scaledLogoImage2));
        }

        leftJPanel.add(imageLabel);
        imageLabel.add(logoLabel);
        imageLabel.add(headingLabel);
        rightJPanel.add(logoLabel2);

        // --- RIGHT PANEL COMPONENTS ---
        JLabel titleLabel = new JLabel("Welcome to Salon Care", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(themeColor);
        titleLabel.setBounds(0, 210, 650, 60);
        rightJPanel.add(titleLabel);

        JLabel subTitleLabel = new JLabel("Please enter your credentials to proceed", SwingConstants.CENTER);
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subTitleLabel.setForeground(themeColor);
        subTitleLabel.setBounds(0, 255, 650, 30);
        rightJPanel.add(subTitleLabel);

        JLabel emailPhoneLabel = new JLabel("Email/Phone: ");
        emailPhoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        emailPhoneLabel.setForeground(Color.black);
        emailPhoneLabel.setBounds(140, 320, 340, 20);
        rightJPanel.add(emailPhoneLabel);

        JTextField emailPhoneField = new JTextField();
        emailPhoneField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emailPhoneField.setBackground(Color.WHITE);
        emailPhoneField.setBorder(new LineBorder(Color.black, 1));
        emailPhoneField.setBounds(140, 350, 380, 35);
        rightJPanel.add(emailPhoneField);

        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        passwordLabel.setForeground(Color.black);
        passwordLabel.setBounds(140, 400, 340, 20);
        rightJPanel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(new LineBorder(Color.black, 1));
        passwordField.setBounds(140, 430, 380, 35);
        rightJPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 22));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(themeColor);
        loginButton.setBounds(230, 500, 200, 50);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rightJPanel.add(loginButton);

        JLabel feedbackLabel = new JLabel("", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        feedbackLabel.setBounds(100, 570, 450, 30);
        rightJPanel.add(feedbackLabel);

        // Don't have an account link
        JLabel signUp = new JLabel("<html>Don't have account? <u>Sign Up</u></html>", SwingConstants.CENTER);
        signUp.setBounds(230, 620, 200, 15);
        signUp.setFont(new Font("Arial", Font.PLAIN, 11));
        signUp.setForeground(Color.black);
        signUp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rightJPanel.add(signUp);
        signUp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new SignupFrame().setVisible(true);
            }
        });

        // --- ACTION LISTENER ---
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String emailOrPhone = emailPhoneField.getText().trim();
                String password = new String(passwordField.getPassword());

                DataManager dataManager = DataManager.getInstance();
                Client client = dataManager.authenticateClient(emailOrPhone, password);

                if (client != null) {
                    feedbackLabel.setText("Login Successful! Redirecting...");
                    feedbackLabel.setForeground(new Color(34, 139, 34));

                    // Route based on user type
                    if (client.isStaff()) {
                        new SalonStaffDashboard().setVisible(true);
                    } else {
                        new ClientDashboardFrame(client).setVisible(true);
                    }
                    dispose();
                } else {
                    feedbackLabel.setText("Invalid credentials. Please try again.");
                    feedbackLabel.setForeground(Color.RED);
                }

                passwordField.setText("");
            }
        });
    }
}