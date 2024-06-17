package stockly;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EditPurchaseFrame extends JFrame {
    private JTextField kodeField;
    private JTextField tanggalField;
    private JComboBox<String> pemasokComboBox;
    private JButton saveButton;
    private JTable detailTable;
    private DefaultTableModel detailTableModel;
    private int purchaseId;
    private String[] detailColumnNames = {"Kode Produk", "Nama Barang", "Harga", "Jumlah", "Total"};

    public EditPurchaseFrame(int purchaseId) {
        this.purchaseId = purchaseId;

        setTitle("Edit Purchase");
        setSize(800, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel formPanel = new JPanel(new GridLayout(4, 2));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        kodeField = new JTextField(20);
        tanggalField = new JTextField(20);
        pemasokComboBox = new JComboBox<>();
        saveButton = new JButton("Save");

        formPanel.add(new JLabel("Kode:"));
        formPanel.add(kodeField);
        formPanel.add(new JLabel("Tanggal:"));
        formPanel.add(tanggalField);
        formPanel.add(new JLabel("Pemasok:"));
        formPanel.add(pemasokComboBox);
        formPanel.add(new JLabel()); // placeholder for save button alignment
        formPanel.add(saveButton);

        add(formPanel, BorderLayout.NORTH);

        // Detail pembelian table
        detailTableModel = new DefaultTableModel(detailColumnNames, 0);
        detailTable = new JTable(detailTableModel);
        JScrollPane scrollPane = new JScrollPane(detailTable);
        add(scrollPane, BorderLayout.CENTER);

        // Set cell renderer to align center
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < detailTable.getColumnCount(); i++) {
            detailTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        loadPurchaseData();
        loadSuppliers();
        loadDetailTable();

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveData();
            }
        });
    }

    private void loadPurchaseData() {
        try {
            Connection con = Dbconnect.getConnect();
            String query = "SELECT * FROM pembelian WHERE id_pembelian = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, purchaseId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                kodeField.setText(rs.getString("kode"));
                tanggalField.setText(rs.getString("tanggal"));
                int pemasokId = rs.getInt("id_pemasok");
                pemasokComboBox.setSelectedItem(getSupplierName(pemasokId));
            } else {
                JOptionPane.showMessageDialog(this, "Purchase not found.");
                dispose();
            }

            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading purchase data.");
        }
    }

    private void loadSuppliers() {
        try {
            Connection con = Dbconnect.getConnect();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id_pemasok, nama FROM pemasok");

            while (rs.next()) {
                pemasokComboBox.addItem(rs.getString("nama"));
            }

            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading suppliers.");
        }
    }

    private String getSupplierName(int supplierId) {
        String supplierName = "";
        try {
            Connection con = Dbconnect.getConnect();
            String query = "SELECT nama FROM pemasok WHERE id_pemasok = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, supplierId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                supplierName = rs.getString("nama");
            }

            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving supplier name.");
        }
        return supplierName;
    }

    private void loadDetailTable() {
        try {
            Connection con = Dbconnect.getConnect();
            String query = "SELECT dp.jumlah, dp.total, lp.kode, lp.nama, lp.harga_beli " +
                           "FROM detail_pembelian dp " +
                           "JOIN list_produk lp ON dp.id_produk = lp.id_list_produk " +
                           "WHERE dp.id_pembelian = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, purchaseId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        rs.getString("kode"),
                        rs.getString("nama"),
                        rs.getDouble("harga_beli"),
                        rs.getInt("jumlah"),
                        rs.getDouble("total")
                };
                detailTableModel.addRow(row);
            }

            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading purchase details.");
        }
    }

    private void saveData() {
        String kode = kodeField.getText();
        String tanggal = tanggalField.getText();
        int selectedPemasokIndex = pemasokComboBox.getSelectedIndex() + 1; // +1 because combo box index starts from 0
        int pemasokId = getPemasokId(selectedPemasokIndex);

        try {
            Connection con = Dbconnect.getConnect();
            String query = "UPDATE pembelian SET kode = ?, tanggal = ?, id_pemasok = ? WHERE id_pembelian = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, kode);
            pstmt.setDate(2, java.sql.Date.valueOf(tanggal));
            pstmt.setInt(3, pemasokId);
            pstmt.setInt(4, purchaseId);
            pstmt.executeUpdate();

            con.close();
            JOptionPane.showMessageDialog(this, "Purchase updated successfully.");
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving data.");
        }
    }

    private int getPemasokId(int index) {
        int pemasokId = -1;
        try {
            Connection con = Dbconnect.getConnect();
            String query = "SELECT id_pemasok FROM pemasok LIMIT ?, 1"; // Assuming id_pemasok is ordered sequentially
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, index);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                pemasokId = rs.getInt("id_pemasok");
            }

            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving supplier ID.");
        }
        return pemasokId;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new EditPurchaseFrame(1).setVisible(true);
            }
        });
    }
}
