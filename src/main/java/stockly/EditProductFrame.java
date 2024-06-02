package stockly;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditProductFrame extends JFrame {
    private JTextField idField;
    private JTextField nameField;
    private JTextField purchasePriceField;
    private JTextField sellingPriceField;
    private JTextField quantityField;
    private JTextField unitField;
    private StockPage stockPage;
    private String productCode;

    public EditProductFrame(StockPage stockPage, String productCode, String name, int quantity, String unit, double purchasePrice, double sellingPrice) {
        this.stockPage = stockPage;
        this.productCode = productCode;

        setTitle("Edit Produk");
        setSize(400, 330);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Edit Produk");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2, 5, 5));

        JLabel idLabel = new JLabel("ID:");
        inputPanel.add(idLabel);
        idField = new JTextField(productCode, 15);
        idField.setPreferredSize(new Dimension(200, 25));
        idField.setEditable(false);
        inputPanel.add(idField);

        JLabel nameLabel = new JLabel("Nama Barang:");
        inputPanel.add(nameLabel);
        nameField = new JTextField(name, 15);
        nameField.setPreferredSize(new Dimension(200, 25));
        inputPanel.add(nameField);

        JLabel purchasePriceLabel = new JLabel("Harga Beli:");
        inputPanel.add(purchasePriceLabel);
        purchasePriceField = new JTextField(String.valueOf(purchasePrice), 15);
        purchasePriceField.setPreferredSize(new Dimension(200, 25));
        inputPanel.add(purchasePriceField);

        JLabel sellingPriceLabel = new JLabel("Harga Jual:");
        inputPanel.add(sellingPriceLabel);
        sellingPriceField = new JTextField(String.valueOf(sellingPrice), 15);
        sellingPriceField.setPreferredSize(new Dimension(200, 25));
        inputPanel.add(sellingPriceField);

        JLabel quantityLabel = new JLabel("Jumlah:");
        inputPanel.add(quantityLabel);
        quantityField = new JTextField(String.valueOf(quantity), 15);
        quantityField.setPreferredSize(new Dimension(200, 25));
        inputPanel.add(quantityField);

        JLabel unitLabel = new JLabel("Satuan:");
        inputPanel.add(unitLabel);
        unitField = new JTextField(unit, 15);
        unitField.setPreferredSize(new Dimension(200, 25));
        inputPanel.add(unitField);

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
                updateProductInDatabase();
            }
        });
        buttonPanel.add(updateButton);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setVisible(true);
    }

    private void updateProductInDatabase() {
        String name = nameField.getText();
        String purchasePrice = purchasePriceField.getText();
        String sellingPrice = sellingPriceField.getText();
        String quantity = quantityField.getText();
        String unit = unitField.getText();

        String updateQuery = "UPDATE list_produk SET nama = ?, stock = ?, satuan = ?, harga_beli = ?, harga_jual = ? WHERE kode = ?";

        try (Connection conn = Dbconnect.getConnect();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, Integer.parseInt(quantity));
            pstmt.setString(3, unit);
            pstmt.setDouble(4, Double.parseDouble(purchasePrice));
            pstmt.setDouble(5, Double.parseDouble(sellingPrice));
            pstmt.setString(6, productCode);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Produk berhasil diupdate!");
                stockPage.refreshTable();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate produk.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan pada database: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format data tidak valid: " + ex.getMessage());
        }
    }
}
