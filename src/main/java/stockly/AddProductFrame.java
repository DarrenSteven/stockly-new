package stockly;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddProductFrame extends JFrame {
    private JTextField idField;
    private JTextField nameField;
    private JTextField purchasePriceField;
    private JTextField sellingPriceField;
    private JTextField quantityField;
    private JTextField unitField;
    private StockPage stockPage;  // Reference to StockPage

    public AddProductFrame(StockPage stockPage) {
        this.stockPage = stockPage;  // Initialize StockPage reference
        setTitle("Tambahkan Produk yang Akan Dijual");
        setSize(400, 330);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel untuk judul
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Tambahkan Produk yang Akan Dijual");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel);

        // Panel untuk input
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2, 5, 5));  // Adjusted layout to include new field

        JLabel idLabel = new JLabel("Kode:");
        inputPanel.add(idLabel);
        idField = new JTextField(15);
        idField.setPreferredSize(new Dimension(200, 25));
        inputPanel.add(idField);

        JLabel nameLabel = new JLabel("Nama Barang:");
        inputPanel.add(nameLabel);
        nameField = new JTextField(15);
        nameField.setPreferredSize(new Dimension(200, 25));
        inputPanel.add(nameField);

        JLabel purchasePriceLabel = new JLabel("Harga Beli:");
        inputPanel.add(purchasePriceLabel);
        purchasePriceField = new JTextField(15);
        purchasePriceField.setPreferredSize(new Dimension(200, 25));
        inputPanel.add(purchasePriceField);

        JLabel sellingPriceLabel = new JLabel("Harga Jual:");
        inputPanel.add(sellingPriceLabel);
        sellingPriceField = new JTextField(15);
        sellingPriceField.setPreferredSize(new Dimension(200, 25));
        inputPanel.add(sellingPriceField);

        JLabel quantityLabel = new JLabel("Jumlah:");
        inputPanel.add(quantityLabel);
        quantityField = new JTextField(15);
        quantityField.setPreferredSize(new Dimension(200, 25));
        inputPanel.add(quantityField);

        JLabel unitLabel = new JLabel("Satuan:");
        inputPanel.add(unitLabel);
        unitField = new JTextField(15);
        unitField.setPreferredSize(new Dimension(200, 25));
        inputPanel.add(unitField);

        // Panel untuk tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Batal");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPanel.add(cancelButton);

        JButton addButton = new JButton("Tambah");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addProductToDatabase();
            }
        });
        buttonPanel.add(addButton);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setVisible(true);
    }

    private void addProductToDatabase() {
        String id = idField.getText();
        String name = nameField.getText();
        String purchasePrice = purchasePriceField.getText();
        String sellingPrice = sellingPriceField.getText();
        String quantity = quantityField.getText();
        String unit = unitField.getText();
    
        String insertQuery = "INSERT INTO list_produk (kode, nama, stock, satuan, harga_beli, harga_jual) VALUES (?, ?, ?, ?, ?, ?)";
    
        try (Connection conn = Dbconnect.getConnect();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
    
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.setInt(3, Integer.parseInt(quantity));
            pstmt.setString(4, unit);
            pstmt.setDouble(5, Double.parseDouble(purchasePrice));
            pstmt.setDouble(6, Double.parseDouble(sellingPrice));
    
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan!");
                stockPage.refreshTable();  // Refresh the table in StockPage
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan produk.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan pada database: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format data tidak valid: " + ex.getMessage());
        }
    }    
}