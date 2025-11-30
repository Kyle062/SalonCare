package gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SignupFrame extends JFrame {

    public SignupFrame() {
        // Theme color matching the provided image's aesthetic
        Color themeColor = new Color(128, 207, 192);

        // Original frame dimensions
        setSize(1300, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        setTitle("Signup Frame");

        // --- LEFT PANEL (Image) ---
        JPanel leftJPanel = new JPanel();
        leftJPanel.setBounds(0, 0, 650, 900);
        leftJPanel.setBackground(new Color(255, 255, 255));
        leftJPanel.setLayout(null);
        add(leftJPanel);

        // --- RIGHT PANEL (Signup Form) ---
        JPanel rightJPanel = new JPanel();
        rightJPanel.setBounds(650, 0, 650, 900);
        rightJPanel.setBackground(Color.WHITE);
        rightJPanel.setLayout(null);
        add(rightJPanel);

        // --- Image Loading (Using original stretching logic) ---
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

        // IMAGES - Corrected class reference to SignupFrame
        URL imageURL = SignupFrame.class.getClassLoader().getResource("images/backgroundLogo.jpg");
        URL imageLogoURL = SignupFrame.class.getClassLoader().getResource("images/LogoFinal1.png");
        URL imageLogoURL2 = SignupFrame.class.getClassLoader().getResource("images/LogoSalonCare2.png");
        
        // Use placeholder images if resources are null (common in Swing)
        ImageIcon originalIcon = (imageURL != null) ? new ImageIcon(imageURL) : new ImageIcon();
        ImageIcon originalLogo = (imageLogoURL != null) ? new ImageIcon(imageLogoURL) : new ImageIcon();
        ImageIcon originalLogo2 = (imageLogoURL2 != null) ? new ImageIcon(imageLogoURL2) : new ImageIcon();

        Image scaledImage = originalIcon.getImage().getScaledInstance(650, 900, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaledImage));

        Image scaledLogoImage = originalLogo.getImage().getScaledInstance(580, 580, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaledLogoImage));

        Image scaledLogoImage2 = originalLogo2.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        logoLabel2.setIcon(new ImageIcon(scaledLogoImage2));

        leftJPanel.add(imageLabel);
        imageLabel.add(logoLabel);
        imageLabel.add(headingLabel);
        rightJPanel.add(logoLabel2);

        // --- RIGHT PANEL COMPONENTS (Updated for Sign Up) ---
        
        // 1. Title and Subtitle
        JLabel titleLabel = new JLabel("Create Your Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(themeColor);
        titleLabel.setBounds(0, 210, 650, 60);
        rightJPanel.add(titleLabel);

        JLabel subTitleLabel = new JLabel("Enter your details to get started with our beauty services system.", SwingConstants.CENTER);
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subTitleLabel.setForeground(new Color(themeColor.getRed() - 30, themeColor.getGreen() - 30, themeColor.getBlue() - 30)); // Slightly darker theme color
        subTitleLabel.setBounds(0, 255, 650, 30);
        rightJPanel.add(subTitleLabel);

        // --- Field Positioning Setup ---
        int xStart = 140;
        int yStart = 310;
        int width = 380;
        int height = 35;
        int spacing = 50; // Vertical spacing between fields
        
        // 2. Name Field
        JLabel nameLabel = new JLabel("Full Name: ");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(Color.black);
        nameLabel.setBounds(xStart, yStart, width, 20);
        rightJPanel.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        nameField.setBackground(Color.WHITE);
        nameField.setBorder(new LineBorder(Color.black, 1));
        nameField.setBounds(xStart, yStart + 30, width, height);
        rightJPanel.add(nameField);

        // 3. Email Field
        JLabel emailLabel = new JLabel("Email Address: ");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        emailLabel.setForeground(Color.black);
        emailLabel.setBounds(xStart, yStart + spacing * 1 + height, width, 20); // 310 + 50 + 35 = 395. Let's adjust to 390
        emailLabel.setBounds(xStart, yStart + 80, width, 20); // 310 + 80 = 390
        rightJPanel.add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emailField.setBackground(Color.WHITE);
        emailField.setBorder(new LineBorder(Color.black, 1));
        emailField.setBounds(xStart, yStart + 110, width, height); // 310 + 110 = 420
        rightJPanel.add(emailField);
        
        // 4. Phone Field
        JLabel phoneLabel = new JLabel("Phone Number: ");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        phoneLabel.setForeground(Color.black);
        phoneLabel.setBounds(xStart, yStart + 160, width, 20); // 310 + 160 = 470
        rightJPanel.add(phoneLabel);

        JTextField phoneField = new JTextField();
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        phoneField.setBackground(Color.WHITE);
        phoneField.setBorder(new LineBorder(Color.black, 1));
        phoneField.setBounds(xStart, yStart + 190, width, height); // 310 + 190 = 500
        rightJPanel.add(phoneField);

        // 5. Password Field
        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        passwordLabel.setForeground(Color.black);
        passwordLabel.setBounds(xStart, yStart + 240, width, 20); // 310 + 240 = 550
        rightJPanel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(new LineBorder(Color.black, 1));
        passwordField.setBounds(xStart, yStart + 270, width, height); // 310 + 270 = 580
        rightJPanel.add(passwordField);

        // 6. Sign Up Button
        JButton signupButton = new JButton("Sign Up");
        signupButton.setFont(new Font("Segoe UI", Font.BOLD, 22));
        signupButton.setForeground(Color.WHITE);
        signupButton.setBackground(themeColor);
        signupButton.setBounds(230, yStart + 340, 200, 50); // 310 + 340 = 650
        signupButton.setFocusPainted(false);
        rightJPanel.add(signupButton);

        // 7. Feedback Label
        JLabel feedbackLabel = new JLabel("", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        feedbackLabel.setBounds(100, yStart + 410, 450, 30); // 310 + 410 = 720
        rightJPanel.add(feedbackLabel);

        // 8. Already have an account link
        JLabel loginLabel = new JLabel("<html>Already have an account? <u>Log In</u></html>", SwingConstants.CENTER);
        loginLabel.setBounds(230, yStart + 460, 200, 15); // 310 + 460 = 770
        loginLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        loginLabel.setForeground(Color.black);
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rightJPanel.add(loginLabel);
        
        // --- ACTION LISTENER for Navigation ---
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        // --- ACTION LISTENER for Sign Up ---
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fullName = nameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String password = new String(passwordField.getPassword());
                
                // Simple validation for mock sign-up
                if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    feedbackLabel.setText("Please fill out all fields.");
                    feedbackLabel.setForeground(Color.RED);
                    return;
                }

                if (password.length() < 6) {
                    feedbackLabel.setText("Password must be at least 6 characters long.");
                    feedbackLabel.setForeground(Color.RED);
                    return;
                }

                // Mock Sign Up successful
                feedbackLabel.setText("Sign Up Successful! Welcome, " + fullName + "!");
                feedbackLabel.setForeground(new Color(34, 139, 34)); // Green color

                // Clear fields on success
                nameField.setText("");
                emailField.setText("");
                phoneField.setText("");
                passwordField.setText("");
            }
        });
    }
    
   
}