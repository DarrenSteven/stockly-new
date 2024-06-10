package stockly;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

public class StockCardFrame extends JFrame {
    private JComboBox<String> productComboBox;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private DefaultTableModel tableModel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private JDatePickerImpl datePickerStart;
    private JDatePickerImpl datePickerEnd;

    public StockCardFrame() {
        setTitle("Kartu Stock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        // Panel untuk section 1: Tanggal Mulai dan Tanggal Akhir
        JPanel dateRangePanel = new JPanel(new GridBagLayout());
        dateRangePanel.setBorder(BorderFactory.createTitledBorder("Rentang Tanggal"));
        dateRangePanel.setPreferredSize(new Dimension(400, 150));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel startDateLabel = new JLabel("Tanggal Mulai:");
        dateRangePanel.add(startDateLabel, gbc);

        gbc.gridx = 1;
        UtilDateModel modelStart = new UtilDateModel();
        JDatePanelImpl datePanelStart = new JDatePanelImpl(modelStart);
        datePickerStart = new JDatePickerImpl(datePanelStart);
        dateRangePanel.add(datePickerStart, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel endDateLabel = new JLabel("Tanggal Akhir:");
        dateRangePanel.add(endDateLabel, gbc);

        gbc.gridx = 1;
        UtilDateModel modelEnd = new UtilDateModel();
        JDatePanelImpl datePanelEnd = new JDatePanelImpl(modelEnd);
        datePickerEnd = new JDatePickerImpl(datePanelEnd);
        dateRangePanel.add(datePickerEnd, gbc);

        // Panel untuk section 2: Cari Nama / ID Barang
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Pilih Barang"));
        searchPanel.setPreferredSize(new Dimension(400, 150));

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel searchItemLabel = new JLabel("Nama Barang:");
        searchPanel.add(searchItemLabel, gbc);

        gbc.gridx = 1;
        productComboBox = new JComboBox<>();
        populateProductComboBox();
        searchPanel.add(productComboBox, gbc);

        // Panel untuk input (section 1 dan section 2)
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(dateRangePanel);
        inputPanel.add(searchPanel);

        // Panel judul dan tanggal
        JPanel titleDatePanel = new JPanel(new BorderLayout());

        JLabel tableTitleLabel = new JLabel("Kartu Detail Stock");
        tableTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tableTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleDatePanel.add(tableTitleLabel, BorderLayout.NORTH);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = dateFormat.format(new Date());
        JLabel dateRangeLabel = new JLabel("Tanggal " + currentDate + " s.d. " + currentDate);
        dateRangeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleDatePanel.add(dateRangeLabel, BorderLayout.CENTER);

        // Panel untuk tombol Generate dan Reset
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton generateButton = new JButton("Generate");
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateStockCard();
            }
        });
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetFields();
            }
        });
        buttonPanel.add(generateButton);
        buttonPanel.add(resetButton);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setPreferredSize(new Dimension(1300, 200));
        topPanel.add(inputPanel);
        topPanel.add(buttonPanel);
        topPanel.add(titleDatePanel);

        contentPanel.add(topPanel, BorderLayout.NORTH);

        // Panel untuk tabel kartu detail stock
        JPanel tablePanel = new JPanel(new BorderLayout());

        String[] columnNames = {"Tgl", "Kode Transaksi", "Stock Masuk", "Stock Keluar", "Stock Sisa"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Make all cells not editable
            }
        };
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(30);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        table.setIntercellSpacing(new Dimension(15, 15));

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(tablePanel, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private void populateProductComboBox() {
        try (Connection conn = Dbconnect.getConnect()) {
            String query = "SELECT nama FROM list_produk";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    productComboBox.addItem(rs.getString("nama"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengambil data produk: " + ex.getMessage());
        }
    }

    private void generateStockCard() {
        // Get values from the date pickers
        Date startDateValue = (Date) datePickerStart.getModel().getValue();
        Date endDateValue = (Date) datePickerEnd.getModel().getValue();
    
        if (startDateValue == null || endDateValue == null) {
            JOptionPane.showMessageDialog(this, "Harap pilih rentang tanggal yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        String startDate = dateFormat.format(startDateValue);
        String endDate = dateFormat.format(endDateValue);
        String selectedProduct = (String) productComboBox.getSelectedItem();
    
        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(this, "Pilih produk terlebih dahulu!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        try (Connection conn = Dbconnect.getConnect()) {
            // Check if table exists
            if (!tableExists(conn, "riwayat_arus_stok")) {
                JOptionPane.showMessageDialog(this, "Table 'riwayat_arus_stok' doesn't exist in the database.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            String query = "SELECT ks.tanggal, " +
                           "COALESCE(pb.kode, pj.kode) AS kode_transaksi, " +
                           "ks.tipe, ks.kuantitas " +
                           "FROM list_produk lp " +
                           "JOIN riwayat_arus_stok ks ON lp.id_list_produk = ks.id_produk " +
                           "LEFT JOIN pembelian pb ON ks.id_pembelian = pb.id_pembelian " +
                           "LEFT JOIN penjualan pj ON ks.id_penjualan = pj.id_penjualan " +
                           "WHERE lp.nama = ? " +
                           "AND ks.tanggal BETWEEN ? AND ? " +
                           "ORDER BY ks.tanggal";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, selectedProduct);
                pstmt.setString(2, startDate);
                pstmt.setString(3, endDate);
    
                try (ResultSet rs = pstmt.executeQuery()) {
                    tableModel.setRowCount(0);  // Clear existing data
    
                    // Calculate initial stock before the start date
                    int initialStock = calculateInitialStock(conn, selectedProduct, startDate);
    
                    // Add initial stock row
                    tableModel.addRow(new Object[]{"Saldo Awal", "", "", "", initialStock});
    
                    int stock = initialStock;
                    while (rs.next()) {
                        String date = rs.getString("tanggal");
                        String transactionCode = rs.getString("kode_transaksi");
                        int quantityIn = rs.getString("tipe").equals("masuk") ? rs.getInt("kuantitas") : 0;
                        int quantityOut = rs.getString("tipe").equals("keluar") ? rs.getInt("kuantitas") : 0;
                        stock += quantityIn - quantityOut;
    
                        tableModel.addRow(new Object[]{date, transactionCode, quantityIn, quantityOut, stock});
                    }
    
                    // Add final stock row
                    tableModel.addRow(new Object[]{"Saldo Akhir", "", "", "", stock});
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan pada database: " + ex.getMessage());
        }
    }
        

    private int calculateInitialStock(Connection conn, String productName, String startDate) throws SQLException {
        String query = "SELECT ks.tipe, ks.kuantitas, lp.stock " +
                       "FROM list_produk lp " +
                       "LEFT JOIN riwayat_arus_stok ks ON lp.id_list_produk = ks.id_produk " +
                       "AND ks.tanggal < ? " +
                       "WHERE lp.nama = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, startDate);
            pstmt.setString(2, productName);

            try (ResultSet rs = pstmt.executeQuery()) {
                int initialStock = 0;
                boolean isFirstRow = true;

                while (rs.next()) {
                    if (isFirstRow) {
                        initialStock = rs.getInt("stock");
                        isFirstRow = false;
                    }

                    String type = rs.getString("tipe");
                    int quantity = rs.getInt("kuantitas");
                    if (type != null) {
                        if (type.equals("masuk")) {
                            initialStock += quantity;
                        } else if (type.equals("keluar")) {
                            initialStock -= quantity;
                        }
                    }
                }
                return initialStock;
            }
        }
    }

    private boolean tableExists(Connection conn, String tableName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null)) {
            return rs.next();
        }
    }

    private void resetFields() {
        startDateSpinner.setValue(new Date());
        endDateSpinner.setValue(new Date());
        productComboBox.setSelectedIndex(0);
        tableModel.setRowCount(0);
    }

    public static void main(String[] args) {
        new StockCardFrame();
    }
}
