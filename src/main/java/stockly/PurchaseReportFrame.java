package stockly;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PurchaseReportFrame extends JFrame {
    private JComboBox<String> searchItemComboBox;
    private JComboBox<String> searchVendorComboBox;
    private DefaultTableModel tableModel;
    private JTextField startDateTextField;
    private JTextField endDateTextField;

    public PurchaseReportFrame() {
        setTitle("Laporan Pembelian");
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
        JPanel dateRangePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        dateRangePanel.setBorder(BorderFactory.createTitledBorder("Rentang Tanggal"));
        dateRangePanel.setPreferredSize(new Dimension(300, 100));

        JPanel startDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel startDateLabel = new JLabel("Tanggal Mulai:");
        startDateTextField = new JTextField(10);
        startDatePanel.add(startDateLabel);
        startDatePanel.add(startDateTextField);

        JPanel endDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel endDateLabel = new JLabel("Tanggal Akhir:");
        endDateTextField = new JTextField(10);
        endDatePanel.add(endDateLabel);
        endDatePanel.add(endDateTextField);

        dateRangePanel.add(startDatePanel);
        dateRangePanel.add(endDatePanel);

        // Panel untuk section 2: Cari Nama Barang dan Nama Supplier
        JPanel searchPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Cari"));
        searchPanel.setPreferredSize(new Dimension(300, 100));

        // Dropdown untuk Nama Barang
        JPanel searchItemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchItemLabel = new JLabel("Nama Barang:");
        List<String> productNames = getProductNames();
        productNames.add(0, "Semua");
        searchItemComboBox = new JComboBox<>(productNames.toArray(new String[0]));
        searchItemPanel.add(searchItemLabel);
        searchItemPanel.add(searchItemComboBox);

        // Dropdown untuk Nama Supplier
        JPanel searchVendorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchVendorLabel = new JLabel("Nama Supplier:");
        List<String> supplierNames = getSupplierNames();
        supplierNames.add(0, "Semua");
        searchVendorComboBox = new JComboBox<>(supplierNames.toArray(new String[0]));
        searchVendorPanel.add(searchVendorLabel);
        searchVendorPanel.add(searchVendorComboBox);

        searchPanel.add(searchItemPanel);
        searchPanel.add(searchVendorPanel);

        // Panel untuk input (section 1 dan section 2)
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(dateRangePanel);
        inputPanel.add(searchPanel);

        // Panel judul dan tanggal
        JPanel titleDatePanel = new JPanel(new BorderLayout());

        JLabel tableTitleLabel = new JLabel("Laporan Pembelian");
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
        JButton resetButton = new JButton("Reset");

        JButton printButton = new JButton("Print");
        buttonPanel.add(generateButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(printButton);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setPreferredSize(new Dimension(1300, 200));
        topPanel.add(inputPanel);
        topPanel.add(buttonPanel);
        topPanel.add(titleDatePanel);

        contentPanel.add(topPanel, BorderLayout.NORTH);

        // Panel untuk tabel laporan pembelian
        JPanel tablePanel = new JPanel(new BorderLayout());

        String[] columnNames = {"Tanggal", "Kode Pembelian", "Nama Barang", "Nama Supplier", "Total"};
        tableModel = new DefaultTableModel(columnNames, 0);

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

        // Action listener untuk tombol "Generate"
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTable();
            }
        });

        setVisible(true);
    }

    private void updateTable() {
        String selectedItem = (String) searchItemComboBox.getSelectedItem();
        String selectedVendor = (String) searchVendorComboBox.getSelectedItem();
        String startDate = startDateTextField.getText().trim();
        String endDate = endDateTextField.getText().trim();

        StringBuilder queryBuilder = new StringBuilder("SELECT pembelian.tanggal, pembelian.kode, list_produk.nama AS nama_barang, pemasok.nama AS nama_supplier, 0 AS total " +
                "FROM pembelian " +
                "JOIN detail_pembelian ON pembelian.id_pembelian = detail_pembelian.id_pembelian " +
                "JOIN list_produk ON detail_pembelian.id_produk = list_produk.id_list_produk " +
                "JOIN pemasok ON pembelian.id_pemasok = pemasok.id_pemasok WHERE 1=1");

        if (!"Semua".equals(selectedItem)) {
            queryBuilder.append(" AND list_produk.nama = '").append(selectedItem).append("'");
        }

        if (!"Semua".equals(selectedVendor)) {
            queryBuilder.append(" AND pemasok.nama = '").append(selectedVendor).append("'");
        }

        // Buat format tanggal yang sesuai untuk kueri SQL
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            queryBuilder.append(" AND pembelian.tanggal BETWEEN '").append(startDate).append("' AND '").append(endDate).append("'");
            }
            String query = queryBuilder.toString();

            tableModel.setRowCount(0);
        
            try (Connection connection = Dbconnect.getConnect();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    String tanggal = resultSet.getString("tanggal");
                    String kodePembelian = resultSet.getString("kode");
                    String namaSupplier = resultSet.getString("nama_supplier");
                    String namaBarang = resultSet.getString("nama_barang");
                    String total = resultSet.getString("total");
        
                    tableModel.addRow(new Object[]{tanggal, kodePembelian, namaBarang, namaSupplier, total});
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        private List<String> getProductNames() {
            List<String> productNames = new ArrayList<>();
            String query = "SELECT nama FROM list_produk";
            try (Connection connection = Dbconnect.getConnect();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    productNames.add(resultSet.getString("nama"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return productNames;
        }
        
        private List<String> getSupplierNames() {
            List<String> supplierNames = new ArrayList<>();
            String query = "SELECT nama FROM pemasok";
            try (Connection connection = Dbconnect.getConnect();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    supplierNames.add(resultSet.getString("nama"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return supplierNames;
        }
        
        public static void main(String[] args) {
            new PurchaseReportFrame();
        }
    }        
