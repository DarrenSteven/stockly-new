package stockly;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddPurchaseFrame extends JFrame {
    private JTable table;
    private JComboBox<String> itemComboBox;
    private JTextField qtyField;
    private JTextField priceField;

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
        JTextField dateField = new JTextField(15);
        JLabel idLabel = new JLabel("Kode Transaksi:");
        JTextField idField = new JTextField(15);
        JLabel supplierLabel = new JLabel("Supplier:");
        JComboBox<String> supplierComboBox = new JComboBox<>(getSupplierNames().toArray(new String[0]));

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

        // Tombol Tambah
        JButton tambahButton = new JButton("Tambah");
        tambahButton.addActionListener(this::tambahButtonActionPerformed);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(tambahButton, BorderLayout.EAST);
        inputPanel.add(buttonPanel, BorderLayout.SOUTH);

        contentPanel.add(inputPanel, BorderLayout.NORTH);

        // Tabel untuk menampilkan data
        String[] columnNames = {"Nama Barang", "Harga", "Jumlah", "Total Harga"};
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

        bottomPanel.add(actionButtonPanel, BorderLayout.SOUTH);

        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);

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

    private int getProductPrice(String productName) {
        int hargaBeli = 0;
        String query = "SELECT harga_beli FROM list_produk WHERE nama = ?";
        try (Connection conn = Dbconnect.getConnect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, productName);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                hargaBeli = resultSet.getInt("harga_beli");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return hargaBeli;
    }

    private void addPurchaseDetail(String productName, int jumlah) {
        String query = "INSERT INTO detail_pembelian (id_produk, jumlah) VALUES (?, ?)";
        try (Connection conn = Dbconnect.getConnect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, getProductId(productName));
            stmt.setInt(2, jumlah);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadTableData() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        String query = "SELECT lp.nama, lp.harga_beli, dp.jumlah, (lp.harga_beli * dp.jumlah) AS total_harga " +
                "FROM detail_pembelian dp JOIN list_produk lp ON dp.id_produk = lp.id_list_produk";
        try (Connection conn = Dbconnect.getConnect();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(query)) {
            while (resultSet.next()) {
                String productName = resultSet.getString("nama");
                int hargaBeli = resultSet.getInt("harga_beli");
                int jumlah = resultSet.getInt("jumlah");
                int totalHarga = resultSet.getInt("total_harga");
                model.addRow(new Object[]{productName, "Rp" + hargaBeli, jumlah, "Rp" + totalHarga});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private int getProductId(String productName) {
        int productId = 0;
        String query = "SELECT id_list_produk FROM list_produk WHERE nama = ?";
        try (Connection conn = Dbconnect.getConnect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, productName);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                productId = resultSet.getInt("id_list_produk");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return productId;
    }

    private void tambahButtonActionPerformed(ActionEvent e) {
        String productName = (String) itemComboBox.getSelectedItem();
        int jumlah = Integer.parseInt(qtyField.getText());
        int hargaBeli = getProductPrice(productName);
        int totalHarga = jumlah * hargaBeli;

        addPurchaseDetail(productName, jumlah);

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(new Object[]{productName, "Rp" + hargaBeli, jumlah, "Rp" + totalHarga});

        qtyField.setText("");
        priceField.setText("");
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

