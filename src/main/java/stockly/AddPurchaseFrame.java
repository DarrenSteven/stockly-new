package stockly;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddPurchaseFrame extends JFrame {
    private JTable table;
    private JTextField dateField, idField, qtyField;
    private JComboBox<String> supplierComboBox, itemComboBox;
    private JTextField priceField;
    private JTextField subtotalTextField;
    private JTextField totalTextField;

    public AddPurchaseFrame() {
        setTitle("Tambah Pembelian");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1300, 850);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Sidebar
        Sidebar sidebar = new Sidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Panel untuk konten utama
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel input
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel judul
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        JLabel titleLabel = new JLabel("Tambah Pembelian");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        inputPanel.add(titlePanel, BorderLayout.NORTH);

        // Panel untuk section 1 dan section 2
        JPanel sectionsPanel = new JPanel(new GridLayout(2, 1, 0, 20)); 
        sectionsPanel.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(10, 10, 10, 10))); 

        // Section 1: Tanggal, ID Transaksi, dan Supplier
        JPanel dateIdSupplierPanel = new JPanel(new GridLayout(3, 2, 5, 5)); 

        JLabel dateLabel = new JLabel("Tanggal:");
        dateField = new JTextField(15);
        JLabel idLabel = new JLabel("Kode Pembelian:");
        idField = new JTextField(15);
        idField.setText(generatePurchaseCode());
        JLabel supplierLabel = new JLabel("Supplier:");
        supplierComboBox = new JComboBox<>(getSupplierNames().toArray(new String[0]));

        dateIdSupplierPanel.add(dateLabel);
        dateIdSupplierPanel.add(dateField);
        dateIdSupplierPanel.add(idLabel);
        dateIdSupplierPanel.add(idField);
        dateIdSupplierPanel.add(supplierLabel);
        dateIdSupplierPanel.add(supplierComboBox);

        sectionsPanel.add(dateIdSupplierPanel);

        // Section 2: Pencarian barang, jumlah, dan harga
        JPanel itemPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        JLabel itemLabel = new JLabel("Cari Barang:");
        itemComboBox = new JComboBox<>(getProductNames().toArray(new String[0]));
        JLabel qtyLabel = new JLabel("Jumlah:");
        qtyField = new JTextField(5);
        JLabel priceLabel = new JLabel("Harga:");
        priceField = new JTextField(5); 
        priceField.setEnabled(false); 
        itemPanel.add(itemLabel);
        itemPanel.add(itemComboBox);
        itemPanel.add(qtyLabel);
        itemPanel.add(qtyField);
        itemPanel.add(priceLabel);
        itemPanel.add(priceField);
        sectionsPanel.add(itemPanel);

        inputPanel.add(sectionsPanel, BorderLayout.CENTER);

        // Panel untuk tombol Tambah
        JPanel buttonPanel = new JPanel(new BorderLayout());
        JButton tambahButton = new JButton("Tambah");
        buttonPanel.add(tambahButton, BorderLayout.EAST); 

        inputPanel.add(buttonPanel, BorderLayout.SOUTH);

        contentPanel.add(inputPanel, BorderLayout.NORTH);

        // Tabel untuk menampilkan data
        String[] columnNames = {"Kode Produk", "Nama Barang", "Harga", "Jumlah", "Total Harga"};
        DefaultTableModel model = new DefaultTableModel(null, columnNames);
        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                if (!(renderer instanceof HeaderRenderer)) {
                    ((JComponent) component).setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                    if (column != 1) {
                        ((JLabel) component).setHorizontalAlignment(SwingConstants.CENTER);
                    }
                }
                return component;
            }
        };
        table.setRowHeight(30); 
        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel untuk subtotal, diskon, dan total
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel subtotalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JLabel subtotalLabel = new JLabel("Subtotal:");
        subtotalTextField = new JTextField(10); // Inisialisasi subtotalTextField
        subtotalTextField.setEditable(false);      

        JLabel diskonLabel = new JLabel("Diskon:");
        JTextField diskonTextField = new JTextField("Rp0", 10); 
        diskonTextField.setEditable(false); 

        JLabel totalLabel = new JLabel("Total:");
        totalTextField = new JTextField(10); 
        totalTextField.setEditable(false); 

        subtotalPanel.add(subtotalLabel);
        subtotalPanel.add(subtotalTextField);
        subtotalPanel.add(diskonLabel);
        subtotalPanel.add(diskonTextField);
        subtotalPanel.add(totalLabel);
        subtotalPanel.add(totalTextField);

        bottomPanel.add(subtotalPanel, BorderLayout.NORTH);

        // Panel untuk tombol Hapus, Batal, Simpan
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton hapusButton = new JButton("Hapus");
        JButton batalButton = new JButton("Batal");
        JButton simpanButton = new JButton("Simpan");
        actionButtonPanel.add(hapusButton);
        actionButtonPanel.add(batalButton);
        actionButtonPanel.add(simpanButton);

        bottomPanel.add(actionButtonPanel, BorderLayout.SOUTH);

        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);

        // Listener untuk tombol Tambah
        tambahButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItemToPurchase();
            }
        });

        // Listener untuk tombol Simpan
        simpanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePurchase(dateField.getText(), idField.getText(), (String) supplierComboBox.getSelectedItem());
            }
        });

        // Listener untuk perubahan pada qtyField
        qtyField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updatePriceField();
            }
        });

        // Listener untuk perubahan pada itemComboBox
        itemComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePriceField();
            }
        });

        // Load data dari database ke tabel
        loadTableData();
    }

    private List<String> getSupplierNames() {
        List<String> supplierNames = new ArrayList<>();
        String query = "SELECT nama FROM pemasok";
        try {
            ResultSet resultSet = Dbconnect.getData(query);
            while (resultSet != null && resultSet.next()) {
                String namaSupplier = resultSet.getString("nama");
                supplierNames.add(namaSupplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return supplierNames;
    }

    private List<String> getProductNames() {
        List<String> productNames = new ArrayList<>();
        String query = "SELECT nama FROM list_produk";
        try {
            ResultSet resultSet = Dbconnect.getData(query);
            while (resultSet != null && resultSet.next()) {
                String productName = resultSet.getString("nama");
                productNames.add(productName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productNames;
    }

    private void addItemToPurchase() {
        String selectedItem = (String) itemComboBox.getSelectedItem();
        int qty = Integer.parseInt(qtyField.getText());
        int price = Integer.parseInt(priceField.getText());
        int totalPrice = qty * price;
    
        int productId = getProductIdByName(selectedItem);
    
        String query = "INSERT INTO temp_detail_pembelian (id_produk, jumlah, total) VALUES (?, ?, ?)";
        try (Connection connection = Dbconnect.getConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, productId);
            preparedStatement.setInt(2, qty);
            preparedStatement.setInt(3, totalPrice);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Barang berhasil ditambahkan ke pembelian.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTableData(); 
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan barang ke pembelian.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saat menambahkan barang ke pembelian: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }    

    private int getProductIdByName(String productName) {
        int productId = -1;
        String query = "SELECT id_list_produk FROM list_produk WHERE nama = ?";
        try (Connection connection = Dbconnect.getConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, productName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                productId = resultSet.getInt("id_list_produk");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productId;
    }

    private void savePurchase(String date, String kodeTransaksi, String supplierName) {
        // Mendapatkan ID pemasok berdasarkan nama
        int supplierId = getSupplierIdByName(supplierName);
        if (supplierId == -1) {
            JOptionPane.showMessageDialog(this, "Supplier tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Menyimpan pembelian baru dan mendapatkan ID pembelian yang baru disimpan
        int purchaseId = saveNewPurchase(date, kodeTransaksi, supplierId);
    
        // Jika pembelian berhasil disimpan, pindahkan data dari temp_detail_pembelian ke detail_pembelian
        if (purchaseId != -1) {
            moveDataFromTempToDetail(purchaseId);
        }
    }

    private void moveDataFromTempToDetail(int purchaseId) {
        String moveQuery = "INSERT INTO detail_pembelian (id_produk, id_pembelian, jumlah, total) " +
                           "SELECT id_produk, ?, jumlah, total FROM temp_detail_pembelian";
        String deleteTempQuery = "DELETE FROM temp_detail_pembelian";
        
        try (Connection connection = Dbconnect.getConnect();
             PreparedStatement moveStatement = connection.prepareStatement(moveQuery);
             PreparedStatement deleteStatement = connection.prepareStatement(deleteTempQuery)) {
            moveStatement.setInt(1, purchaseId);
            
            // Memindahkan data dari temp_detail_pembelian ke detail_pembelian
            int rowsMoved = moveStatement.executeUpdate();
            
            // Menghapus semua data dari temp_detail_pembelian setelah dipindahkan
            int rowsDeleted = deleteStatement.executeUpdate();
            
            // Jika berhasil memindahkan dan menghapus data
            if (rowsMoved > 0 && rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Data pembelian berhasil disimpan.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTableData(); // Refresh table data after successful insertion
                dispose(); // Close the AddPurchaseFrame
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data pembelian.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saat menyimpan data pembelian: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }    

    private int saveNewPurchase(String date, String kodeTransaksi, int supplierId) {
        int purchaseId = -1;
        String query = "INSERT INTO pembelian (tanggal, kode, id_pemasok) VALUES (?, ?, ?)";
        try (Connection connection = Dbconnect.getConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, date);
            preparedStatement.setString(2, kodeTransaksi);
            preparedStatement.setInt(3, supplierId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    purchaseId = generatedKeys.getInt(1);
                }
                JOptionPane.showMessageDialog(this, "Data pembelian berhasil disimpan.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTableData(); // Refresh table data after successful insertion
                dispose(); // Close the AddPurchaseFrame
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data pembelian.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saat menyimpan data pembelian: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return purchaseId;
    }

    private int getSupplierIdByName(String supplierName) {
        int supplierId = -1;
        String query = "SELECT id_pemasok FROM pemasok WHERE nama = ?";
        try (Connection connection = Dbconnect.getConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, supplierName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                supplierId = resultSet.getInt("id_pemasok");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return supplierId;
    }

    private int getProductPriceByName(String productName) {
        int productPrice = 0;
        String query = "SELECT harga_beli FROM list_produk WHERE nama = ?";
        try (Connection connection = Dbconnect.getConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, productName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                productPrice = resultSet.getInt("harga_beli");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productPrice;
    }

    private void updatePriceField() {
        String selectedItem = (String) itemComboBox.getSelectedItem();
        int qty = 0;
        try {
            qty = Integer.parseInt(qtyField.getText());
        } catch (NumberFormatException e) {
            // Ignore invalid number format
        }
        int pricePerUnit = getProductPriceByName(selectedItem);
        int totalPrice = pricePerUnit * qty;
        priceField.setText(String.valueOf(totalPrice));
    }

    private void loadTableData() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear existing data
    
        String query = "SELECT lp.kode, lp.nama, lp.harga_beli, dp.jumlah, dp.total FROM temp_detail_pembelian dp JOIN list_produk lp ON dp.id_produk = lp.id_list_produk";
        try {
            ResultSet resultSet = Dbconnect.getData(query);
            while (resultSet != null && resultSet.next()) {
                String kode = resultSet.getString("kode");
                String namaBarang = resultSet.getString("nama");
                int harga = resultSet.getInt("harga_beli");
                int jumlah = resultSet.getInt("jumlah");
                int totalHarga = resultSet.getInt("total");
                model.addRow(new Object[]{kode, namaBarang, "Rp" + harga, jumlah, "Rp" + totalHarga});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        // Update subtotal
        int subtotal = calculateSubtotal();
        subtotalTextField.setText("Rp" + subtotal);
        totalTextField.setText("Rp" + subtotal);
    }

    private int calculateSubtotal() {
        int subtotal = 0;
        String query = "SELECT SUM(total) AS subtotal FROM temp_detail_pembelian";
        try (Connection connection = Dbconnect.getConnect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                subtotal = resultSet.getInt("subtotal");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subtotal;
    }

    private String generatePurchaseCode() {
        String lastPurchaseCode = getLastPurchaseCode();
        if (lastPurchaseCode != null) {
            // Ambil angka dari kode terakhir dan tambahkan satu
            int lastCodeNumber = Integer.parseInt(lastPurchaseCode.substring(2)); // Dapatkan angka setelah "PB"
            String newCodeNumber = String.format("%02d", lastCodeNumber + 1); // Format dengan dua digit
            return "PB" + newCodeNumber;
        } else {
            // Jika tidak ada kode pembelian sebelumnya, mulai dari PB01
            return "PB01";
        }
    }
    
    private String getLastPurchaseCode() {
        String lastPurchaseCode = null;
        String query = "SELECT kode FROM pembelian ORDER BY id_pembelian DESC LIMIT 1";
        try (Connection connection = Dbconnect.getConnect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                lastPurchaseCode = resultSet.getString("kode");
            }
        } catch (SQLException e) { 
            e.printStackTrace();
        }
        return lastPurchaseCode;
    }    

    private static class HeaderRenderer extends DefaultTableCellRenderer {
        public HeaderRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }
    }

    public static void main(String[] args) {
        new AddPurchaseFrame();
    }
}
