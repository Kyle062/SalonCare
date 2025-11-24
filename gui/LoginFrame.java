package gui;

import java.awt.Color;
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

public class LoginFrame extends JFrame {

    private static final String LEFT_IMAGE_PATH = "images/backgroundLogo.jpg";
    private static final String LOGO_IMAGE_PATH = "images/LogoFinal1.png";

    public LoginFrame() {
        Color themeColor = new Color(128, 207, 192);

        // Original frame dimensions
        setSize(1300, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        setTitle("Login Frame");

        // --- LEFT PANEL (Image) ---
        JPanel leftJPanel = new JPanel();
        leftJPanel.setBounds(0, 0, 650, 900);
        leftJPanel.setBackground(new Color(255, 255, 255));
        leftJPanel.setLayout(null);
        add(leftJPanel);

        // --- RIGHT PANEL (Login Form) ---
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

        JLabel headingLabel = new JLabel(
                "<html>DYNAMIC CLIENT SCHEDULING SYSTEM <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;FOR BEAUTY SERVICES</html>");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 19));
        headingLabel.setBounds(150, 515, 580, 50);

            URL imageURL = LoginFrame.class.getClassLoader().getResource(LEFT_IMAGE_PATH);
            URL imageLogoURL = LoginFrame.class.getClassLoader().getResource(LOGO_IMAGE_PATH);
            if (imageURL != null) {
                ImageIcon originalIcon = new ImageIcon(imageURL);
                ImageIcon originalLogo = new ImageIcon(imageLogoURL);

                Image scaledImage = originalIcon.getImage().getScaledInstance(
                        650, 900, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));

                Image scaledLogoImage = originalLogo.getImage().getScaledInstance(580, 580, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(scaledLogoImage));

    
        leftJPanel.add(imageLabel);
        imageLabel.add(logoLabel);
        imageLabel.add(headingLabel);

        // --- RIGHT PANEL COMPONENTS ---
        JLabel titleLabel = new JLabel("Welcome to Salon Care", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        titleLabel.setForeground(themeColor);
        titleLabel.setBounds(0, 150, 650, 60);
        rightJPanel.add(titleLabel);

        JLabel subTitleLabel = new JLabel("Please enter your credentials to proceed", SwingConstants.CENTER);
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subTitleLabel.setForeground(themeColor);
        subTitleLabel.setBounds(0, 210, 650, 30);
        rightJPanel.add(subTitleLabel);

        JLabel emailPhoneLabel = new JLabel("Email/Phone: ");
        emailPhoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emailPhoneLabel.setForeground(Color.black);
        emailPhoneLabel.setBounds(100, 320, 450, 25);
        rightJPanel.add(emailPhoneLabel);

        JTextField emailPhoneField = new JTextField();
        emailPhoneField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emailPhoneField.setBackground(Color.WHITE);
        emailPhoneField.setBorder(new LineBorder(Color.black, 1));
        emailPhoneField.setBounds(100, 350, 450, 40);
        rightJPanel.add(emailPhoneField);

        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordLabel.setForeground(Color.black);
        passwordLabel.setBounds(100, 430, 450, 25);
        rightJPanel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(new LineBorder(Color.black, 1));
        passwordField.setBounds(100, 460, 450, 40);
        rightJPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 22));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(themeColor);
        loginButton.setBounds(100, 560, 450, 55);
        loginButton.setFocusPainted(false);
        rightJPanel.add(loginButton);

        JLabel feedbackLabel = new JLabel("", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        feedbackLabel.setBounds(100, 630, 450, 30);
        rightJPanel.add(feedbackLabel);

        // --- ACTION LISTENER ---
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String emailOrPhone = emailPhoneField.getText().trim();
                String password = new String(passwordField.getPassword());

                if (emailOrPhone.equals("user@saloncare.com") && password.equals("1234")) {
                    feedbackLabel.setText("Login Successful! Redirecting...");
                    feedbackLabel.setForeground(new Color(34, 139, 34));

                } else {
                    feedbackLabel.setText("Invalid credentials. Please try again.");
                    feedbackLabel.setForeground(Color.RED);
                }

                passwordField.setText("");
            }
        });
    }

}