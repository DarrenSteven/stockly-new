package stockly;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddSaleFrame extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField dateField, idField, qtyField;
    private JComboBox<String> itemComboBox;
    private JTextField priceField;

    public AddSaleFrame() {
        setTitle("Tambah Penjualan");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1300, 850);
        setLocationRelativeTo(null);

        // Panel utama untuk konten dan sidebar
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
        JLabel titleLabel = new JLabel("Tambah Penjualan");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        inputPanel.add(titlePanel, BorderLayout.NORTH);

        // Panel untuk section 1 dan section 2
        JPanel sectionsPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        sectionsPanel.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(10, 10, 10, 10)));

        // Section 1: Tanggal, ID Transaksi, dan Customer
        JPanel dateIdCustomerPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        JLabel dateLabel = new JLabel("Tanggal:");
        dateField = new JTextField(15);
        JLabel idLabel = new JLabel("Kode Penjualan:");
        idField = new JTextField(15);
        JLabel customerLabel = new JLabel("Customer:");
        JTextField customerField = new JTextField("Cash");
        customerField.setEditable(false);

        // Tambahkan elemen ke dalam panel
        dateIdCustomerPanel.add(dateLabel);
        dateIdCustomerPanel.add(dateField);
        dateIdCustomerPanel.add(idLabel);
        dateIdCustomerPanel.add(idField);
        dateIdCustomerPanel.add(customerLabel);
        dateIdCustomerPanel.add(customerField);

        sectionsPanel.add(dateIdCustomerPanel);

        // Section 2: Pencarian barang, jumlah, dan harga
        JPanel itemPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        JLabel itemLabel = new JLabel("Cari Barang:");
        itemComboBox = new JComboBox<>(getProductNames().toArray(new String[0]));
        JLabel qtyLabel = new JLabel("Jumlah:");
        qtyField = new JTextField(5);
        JLabel priceLabel = new JLabel("Harga:");
        priceField = new JTextField(5);
        priceField.setEnabled(false);
      // Tambahkan event listener untuk itemComboBox
        itemComboBox.addActionListener(e -> {
            String selectedItem = (String) itemComboBox.getSelectedItem();
            // Ambil harga produk berdasarkan nama produk yang dipilih
            String productPrice = getProductPriceByProductName(selectedItem);
            // Setel harga produk ke priceField
            priceField.setText(productPrice);
        });

      
    
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
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel) {
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
        JTextField subtotalTextField = new JTextField("Rp0", 10);
        subtotalTextField.setEditable(false);
        JLabel diskonLabel = new JLabel("Diskon:");
        JTextField diskonTextField = new JTextField("Rp0", 10);
        diskonTextField.setEditable(false);
        JLabel totalLabel = new JLabel("Total:");
        JTextField totalTextField = new JTextField("Rp0", 10);
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

        // Tambahkan event listener untuk tombol Simpan
        simpanButton.addActionListener(e -> {
            String tanggal = dateField.getText();
            String kodePenjualan = idField.getText();
            simpanDataKeDatabase(tanggal, kodePenjualan);
        });

        // Tambahkan event listener untuk tombol Tambah
        tambahButton.addActionListener(e -> {
            String kodePenjualan = idField.getText();
            int idPenjualan = getSalesIdBySalesCode(kodePenjualan);
            String namaProduk = (String) itemComboBox.getSelectedItem();
            int barang = getIdProdukByNamaBarang(namaProduk);
            int jumlah = Integer.parseInt(qtyField.getText());
            simpanDetailPenjualanKeDatabase(idPenjualan, barang, jumlah);
            loadDataFromDatabase();
        });
        

        bottomPanel.add(actionButtonPanel, BorderLayout.SOUTH);

        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);

        // Load data from database
        loadDataFromDatabase();
    }

    private void simpanDataKeDatabase(String tanggal, String kodePenjualan) {
        try (Connection connection = Dbconnect.getConnect()) {
            String insertQuery = "INSERT INTO penjualan (kode, tanggal) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, kodePenjualan);
            preparedStatement.setString(2, tanggal);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan ke database.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadDataFromDatabase(); // Memanggil metode loadDataFromDatabase() untuk refresh tampilan
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data ke database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menyimpan data ke database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void simpanDetailPenjualanKeDatabase(int id_penjualan, int barang, int jumlah) {
        System.out.println(id_penjualan);
        System.out.println(barang);
        System.out.println(jumlah);
        try (Connection connection = Dbconnect.getConnect()) {
            String insertQuery = "INSERT INTO detail_penjualan (id_penjualan, id_produk, jumlah, diskon) VALUES (?,?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setInt(1, id_penjualan);
            preparedStatement.setInt(2, barang);
            preparedStatement.setInt(3, jumlah);
            preparedStatement.setInt(4, 0);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan ke database.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data ke database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menyimpan data ke database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getIdProdukByNamaBarang(String barang) {
        try (Connection connection = Dbconnect.getConnect()) {
            String query = "SELECT id_list_produk FROM list_produk WHERE nama = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, barang);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id_list_produk");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Jika terjadi kesalahan atau barang tidak ditemukan
    }

    private void loadDataFromDatabase() {
        tableModel.setRowCount(0);
        
        try (Connection connection = Dbconnect.getConnect()) {
            Statement statement = connection.createStatement();
            String productQuery = "SELECT id_list_produk, kode, nama, harga_jual FROM list_produk";
            ResultSet productResultSet = statement.executeQuery(productQuery);

            List<Product> products = new ArrayList<>();
            while (productResultSet.next()) {
                int id = productResultSet.getInt("id_list_produk");
                String kode = productResultSet.getString("kode");
                String nama = productResultSet.getString("nama");
                double harga = productResultSet.getDouble("harga_jual");
                products.add(new Product(id, kode, nama, harga));
            }

            String salesQuery = "SELECT id_produk, jumlah FROM detail_penjualan";
            ResultSet salesResultSet = statement.executeQuery(salesQuery);

            while (salesResultSet.next()) {
                int idProduk = salesResultSet.getInt("id_produk");
                int jumlah = salesResultSet.getInt("jumlah");
                Product product = findProductById(products, idProduk);
                if (product != null) {
                    double totalHarga = product.harga * jumlah;
                    tableModel.addRow(new Object[]{product.kode, product.nama, product.harga, jumlah, totalHarga});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private int getSalesIdBySalesCode(String salesCode) {
        int salesId = -1;
        String salesQuery = "SELECT id_penjualan FROM penjualan WHERE kode = ?";
        try (Connection connection = Dbconnect.getConnect();
             PreparedStatement statement = connection.prepareStatement(salesQuery)) {
            statement.setString(1, salesCode);
            ResultSet salesResultSet = statement.executeQuery();
            if (salesResultSet.next()) {
                salesId = salesResultSet.getInt("id_penjualan");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salesId;
    }



    private String getProductPriceByProductName(String name) {
        String salesQuery = "SELECT harga_jual FROM list_produk WHERE nama = ?";
        try (Connection connection = Dbconnect.getConnect();
             PreparedStatement statement = connection.prepareStatement(salesQuery)) {
            statement.setString(1, name);
            ResultSet productResultSet = statement.executeQuery();
            if (productResultSet.next()) {
                return String.valueOf(productResultSet.getDouble("harga_jual"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
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

    private Product findProductById(List<Product> products, int id) {
        for (Product product : products) {
            if (product.id == id) {
                return product;
            }
        }
        return null;
    }

    private static class Product {
        int id;
        String kode;
        String nama;
        double harga;

        public Product(int id, String kode, String nama, double harga) {
            this.id = id;
            this.kode = kode;
            this.nama = nama;
            this.harga = harga;
        }
    }

    private static class HeaderRenderer extends DefaultTableCellRenderer {
        public HeaderRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }
    }

    public static void main(String[] args) {
        new AddSaleFrame();
    }
}