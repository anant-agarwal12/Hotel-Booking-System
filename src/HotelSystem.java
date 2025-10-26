import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class HotelSystem extends JFrame {
    private JTextField nameField, roomTypeField, checkInField, checkOutField, priceField, extraField;
    private DefaultTableModel tableModel;
    private JTable bookingTable;

    public HotelSystem() {
        setTitle("🏨 Hotel Booking System");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(30, 30, 30));
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Hotel Booking Manager", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 15, 15));
        formPanel.setBackground(new Color(45, 45, 45));
        formPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        Color textColor = Color.WHITE;
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);
        String[] labels = {"Customer Name:", "Room Type:", "Check-in (YYYY-MM-DD):",
                           "Check-out (YYYY-MM-DD):", "Price/Night:", "Extra Charges:"};

        nameField = new JTextField(); roomTypeField = new JTextField();
        checkInField = new JTextField(); checkOutField = new JTextField();
        priceField = new JTextField(); extraField = new JTextField();

        JTextField[] fields = {nameField, roomTypeField, checkInField, checkOutField, priceField, extraField};

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(labelFont);
            lbl.setForeground(textColor);
            formPanel.add(lbl);
            styleField(fields[i]);
            formPanel.add(fields[i]);
        }

        JButton addBtn = styledButton("➕ Add");
        JButton updateBtn = styledButton("✏️ Update");
        JButton delBtn = styledButton("🗑️ Delete");
        JButton loadBtn = styledButton("🔄 Refresh");
        JButton invoiceBtn = styledButton("📄 Invoice");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(new Color(35, 35, 35));
        btnPanel.add(addBtn); btnPanel.add(updateBtn); btnPanel.add(delBtn); btnPanel.add(loadBtn); btnPanel.add(invoiceBtn);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(30, 30, 30));
        top.add(formPanel, BorderLayout.CENTER);
        top.add(btnPanel, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        String[] cols = {"ID", "Name", "Room", "Check-in", "Check-out", "Price", "Extra", "Total"};
        tableModel = new DefaultTableModel(cols, 0);
        bookingTable = new JTable(tableModel);
        styleTable();

        JScrollPane sp = new JScrollPane(bookingTable);
        sp.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                "Bookings", 0, 0, new Font("Segoe UI", Font.BOLD, 16), Color.WHITE));
        sp.getViewport().setBackground(new Color(45, 45, 45));
        add(sp, BorderLayout.CENTER);

        addBtn.addActionListener(e -> addBooking());
        updateBtn.addActionListener(e -> updateBooking());
        delBtn.addActionListener(e -> deleteBooking());
        loadBtn.addActionListener(e -> loadBookings());
        invoiceBtn.addActionListener(e -> generateInvoice());

        // load initially
        loadBookings();
    }

    private void styleField(JTextField f) {
        f.setBackground(new Color(60, 63, 65));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    }

    private JButton styledButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(0, 120, 215));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(new Color(0, 140, 240)); }
            public void mouseExited(java.awt.event.MouseEvent e) { b.setBackground(new Color(0, 120, 215)); }
        });
        return b;
    }

    private void styleTable() {
        bookingTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bookingTable.setRowHeight(25);
        bookingTable.setBackground(new Color(55, 55, 55));
        bookingTable.setForeground(Color.WHITE);
        bookingTable.getTableHeader().setBackground(new Color(70, 70, 70));
        bookingTable.getTableHeader().setForeground(Color.WHITE);
        bookingTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private void addBooking() {
        try (Connection conn = DBConnection.getConnection()) {
            String name = nameField.getText().trim();
            String room = roomTypeField.getText().trim();
            LocalDate in = LocalDate.parse(checkInField.getText().trim());
            LocalDate out = LocalDate.parse(checkOutField.getText().trim());
            double price = Double.parseDouble(priceField.getText());
            double extra = Double.parseDouble(extraField.getText());
            long nights = ChronoUnit.DAYS.between(in, out);
            if (nights <= 0) { JOptionPane.showMessageDialog(this, "Check-out must be after check-in."); return; }
            double total = (price * nights) + extra;

            String sql = "INSERT INTO booking(customer_name, room_type, check_in, check_out, price_per_night, extra_charges, total_amount) VALUES (?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, room);
            ps.setDate(3, java.sql.Date.valueOf(in));
            ps.setDate(4, java.sql.Date.valueOf(out));
            ps.setDouble(5, price);
            ps.setDouble(6, extra);
            ps.setDouble(7, total);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Booking Added (" + nights + " nights)");
            loadBookings();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadBookings() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM booking")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getString("customer_name"),
                        rs.getString("room_type"),
                        rs.getDate("check_in"),
                        rs.getDate("check_out"),
                        rs.getDouble("price_per_night"),
                        rs.getDouble("extra_charges"),
                        rs.getDouble("total_amount")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateBooking() {
        int row = bookingTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a booking to update."); return; }

        int id = (int) tableModel.getValueAt(row, 0);
        try (Connection conn = DBConnection.getConnection()) {
            String name = nameField.getText().trim();
            String room = roomTypeField.getText().trim();
            LocalDate in = LocalDate.parse(checkInField.getText().trim());
            LocalDate out = LocalDate.parse(checkOutField.getText().trim());
            double price = Double.parseDouble(priceField.getText());
            double extra = Double.parseDouble(extraField.getText());
            long nights = ChronoUnit.DAYS.between(in, out);
            if (nights <= 0) { JOptionPane.showMessageDialog(this, "Check-out must be after check-in."); return; }
            double total = (price * nights) + extra;

            String sql = "UPDATE booking SET customer_name=?, room_type=?, check_in=?, check_out=?, price_per_night=?, extra_charges=?, total_amount=? WHERE booking_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name); ps.setString(2, room);
            ps.setDate(3, java.sql.Date.valueOf(in));
            ps.setDate(4, java.sql.Date.valueOf(out));
            ps.setDouble(5, price); ps.setDouble(6, extra);
            ps.setDouble(7, total); ps.setInt(8, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✏️ Booking Updated!");
            loadBookings();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    private void deleteBooking() {
        int row = bookingTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a booking to delete."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM booking WHERE booking_id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "🗑️ Booking Deleted!");
            loadBookings();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    private void generateInvoice() {
        int row = bookingTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a booking to generate invoice."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        String name = tableModel.getValueAt(row, 1).toString();
        String room = tableModel.getValueAt(row, 2).toString();
        String checkIn = tableModel.getValueAt(row, 3).toString();
        String checkOut = tableModel.getValueAt(row, 4).toString();
        String price = tableModel.getValueAt(row, 5).toString();
        String extra = tableModel.getValueAt(row, 6).toString();
        String total = tableModel.getValueAt(row, 7).toString();

        String filename = "invoice_booking_" + id + ".txt";
        try (java.io.PrintWriter out = new java.io.PrintWriter(filename)) {
            out.println("----- Hotel Invoice -----");
            out.println("Booking ID: " + id);
            out.println("Customer: " + name);
            out.println("Room Type: " + room);
            out.println("Check-in: " + checkIn);
            out.println("Check-out: " + checkOut);
            out.println("Price/Night: " + price);
            out.println("Extra Charges: " + extra);
            out.println("Total Amount: " + total);
            out.println("-------------------------");
            JOptionPane.showMessageDialog(this, "Invoice generated: " + filename);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error writing invoice: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelSystem().setVisible(true));
    }
}
