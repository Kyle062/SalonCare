package gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import models.Appointment;

public class AppointmentsPanel extends JPanel {

    // Colors from your Main Dashboard
    final Color mintTeal = new Color(128, 207, 192);
    final Color softRed = new Color(220, 80, 80);
    final Color darkGrayText = new Color(70, 70, 70);
    final Color lightGrayBorder = new Color(220, 220, 220);
    final Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
    final Font headerFont = new Font("Segoe UI", Font.BOLD, 28);

    private LinkedList<Appointment> appointmentList = new LinkedList<>();
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField nameField, contactField, serviceField, dateField, timeField;

    public AppointmentsPanel(int width, int height) {
        setLayout(null);
        setSize(width, height);
        setBackground(Color.WHITE);

        // --- 1. Header Title ---
        JLabel title = new JLabel("My Appointments");
        title.setFont(headerFont);
        title.setForeground(darkGrayText);
        title.setBounds((int) (width * 0.04), 20, 400, 40);
        add(title);

        // --- 2. Styled Table ---
        String[] columnNames = { "Client Name", "Contact", "Service", "Date", "Time" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable editing directly in table
            }
        };

        table = new JTable(tableModel);
        styleTable(table);

        // Click listener to fill text fields
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    nameField.setText(tableModel.getValueAt(row, 0).toString());
                    contactField.setText(tableModel.getValueAt(row, 1).toString());
                    serviceField.setText(tableModel.getValueAt(row, 2).toString());
                    dateField.setText(tableModel.getValueAt(row, 3).toString());
                    timeField.setText(tableModel.getValueAt(row, 4).toString());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds((int) (width * 0.04), 80, width - (int) (width * 0.08), 300);
        scrollPane.setBorder(BorderFactory.createLineBorder(lightGrayBorder, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane);

        // --- 3. Input Form Area ---
        int startY = 410;
        int inputHeight = 35;
        int col1X = (int) (width * 0.04);
        int col2X = (int) (width * 0.5);
        int fieldWidth = (int) (width * 0.4);

        addLabel("Client Name", col1X, startY);
        nameField = createStyledField(col1X, startY + 25, fieldWidth, inputHeight);

        addLabel("Contact Number", col2X, startY);
        contactField = createStyledField(col2X, startY + 25, fieldWidth, inputHeight);

        addLabel("Service Type", col1X, startY + 70);
        serviceField = createStyledField(col1X, startY + 95, fieldWidth, inputHeight);

        addLabel("Date (e.g., Oct 24)", col2X, startY + 70);
        dateField = createStyledField(col2X, startY + 95, fieldWidth, inputHeight);

        addLabel("Time (e.g., 10:00 AM)", col1X, startY + 140);
        timeField = createStyledField(col1X, startY + 165, fieldWidth, inputHeight);

        // --- 4. Action Buttons ---
        int btnY = startY + 165;
        int btnWidth = 140;
        int btnHeight = 40;
        int startBtnX = col2X;

        JButton addBtn = createStyledButton("Book Now", mintTeal, startBtnX, btnY, btnWidth, btnHeight);
        addBtn.addActionListener(e -> addAppointment());
        add(addBtn);

        JButton updateBtn = createStyledButton("Update", new Color(100, 149, 237), startBtnX + btnWidth + 10, btnY,
                btnWidth, btnHeight);
        updateBtn.addActionListener(e -> updateAppointment());
        add(updateBtn);

        JButton deleteBtn = createStyledButton("Cancel", softRed, startBtnX + (btnWidth * 2) + 20, btnY, btnWidth,
                btnHeight);
        deleteBtn.addActionListener(e -> deleteAppointment());
        add(deleteBtn);

        // Search Button (Small, near title)
        JButton searchBtn = createStyledButton("Search", mintTeal, width - 180, 25, 120, 35);
        searchBtn.addActionListener(e -> searchAppointment());
        add(searchBtn);

        // Refresh Button
        JButton refreshBtn = createStyledButton("Refresh", Color.LIGHT_GRAY, width - 310, 25, 120, 35);
        refreshBtn.addActionListener(e -> refreshTable(appointmentList));
        add(refreshBtn);
    }

    // --- LOGIC (Linked List Operations) ---

    private voidaddAppointment() {
        if(nameField.getText().isEmpty() || serviceField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Name and Service.");
            return;
        }
        
        Appointment newAppt = new Appointment(
            nameField.getText(), contactField.getText(), serviceField.getText(), 
            dateField.getText(), timeField.getText()
        );

        appointmentList.add(newAppt);
        refreshTable(appointmentList);
        clearFields();
    }

    private void updateAppointment() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;

        Appointment appt = appointmentList.get(row);
        appt.clientName = nameField.getText();
        appt.contact = contactField.getText();
        appt.service = serviceField.getText();
        appt.date = dateField.getText();
        appt.time = timeField.getText();

        refreshTable(appointmentList);
    }

    private void deleteAppointment() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;

        int confirm = JOptionPane.showConfirmDialog(this, "Cancel this appointment?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            appointmentList.remove(row);
            refreshTable(appointmentList);
            clearFields();
        }
    }

    private void searchAppointment() {
        String query = JOptionPane.showInputDialog(this, "Search by Name:");
        if (query == null || query.trim().isEmpty())
            return;

        LinkedList<Appointment> results = new LinkedList<>();
        for (Appointment appt : appointmentList) {
            if (appt.clientName.toLowerCase().contains(query.toLowerCase())) {
                results.add(appt);
            }
        }
        refreshTable(results);
    }

    private void refreshTable(LinkedList<Appointment> list) {
        tableModel.setRowCount(0);
        for (Appointment appt : list) {
            tableModel.addRow(new Object[] {
                    appt.clientName, appt.contact, appt.service, appt.date, appt.time
            });
        }
    }

    private void clearFields() {
        nameField.setText("");
        contactField.setText("");
        serviceField.setText("");
        dateField.setText("");
        timeField.setText("");
        table.clearSelection();
    }

    // --- STYLING HELPERS ---

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(230, 245, 240));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setBackground(mintTeal);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setOpaque(true);

        // Center text in cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private JTextField createStyledField(int x, int y, int w, int h) {
        JTextField field = new JTextField();
        field.setBounds(x, y, w, h);
        field.setFont(mainFont);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(lightGrayBorder, 1, true),
                new EmptyBorder(5, 10, 5, 10)));
        add(field);
        return field;
    }

    private void addLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(Color.GRAY);
        label.setBounds(x, y, 200, 20);
        add(label);
    }

    private JButton createStyledButton(String text, Color bg, int x, int y, int w, int h) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.darker());
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }
}