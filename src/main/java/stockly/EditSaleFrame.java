package stockly;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditSaleFrame extends JFrame {
    private JTextField saleCodeField;
    private JTextField dateField;
    private SalesListPage salesListPage;
    private String saleCode;

    public EditSaleFrame(SalesListPage salesListPage, String saleCode, String date) {
        this.salesListPage = salesListPage;
        this.saleCode = saleCode;

        setTitle("Edit Penjualan");
        setSize(400, 330);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Edit Penjualan");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2, 5, 5));

        JLabel saleCodeLabel = new JLabel("Kode Penjualan:");
        inputPanel.add(saleCodeLabel);
        saleCodeField = new JTextField(saleCode, 15);
        saleCodeField.setPreferredSize(new Dimension(200, 25));
        saleCodeField.setEditable(false);
        inputPanel.add(saleCodeField);

        JLabel dateLabel = new JLabel("Tanggal Penjualan:");
        inputPanel.add(dateLabel);
        dateField = new JTextField(date, 15);
        dateField.setPreferredSize(new Dimension(200, 25));
        inputPanel.add(dateField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Batal");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPanel.add(cancelButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateSaleInDatabase();
            }
        });
        buttonPanel.add(updateButton);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setVisible(true);
    }

    private void updateSaleInDatabase() {
        String date = dateField.getText();

        String updateQuery = "UPDATE penjualan SET tanggal = ? WHERE kode = ?";

        try (Connection conn = Dbconnect.getConnect();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setString(1, date);
            pstmt.setString(2, saleCode);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Penjualan berhasil diupdate!");
                salesListPage.loadData(); // Memanggil metode loadData() dari SalesListPage setelah update berhasil
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate penjualan.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan pada database: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format data tidak valid: " + ex.getMessage());
        }
    }
}