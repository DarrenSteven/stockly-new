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
import java.util.Locale;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

public class SalesReportFrame extends JFrame {
    private JComboBox<String> searchItemComboBox;
    private DefaultTableModel tableModel;

    private JDatePickerImpl datePickerStart;
    private JDatePickerImpl datePickerEnd;

    private JLabel dateRangeLabel;

    // Indonesian date format
    private SimpleDateFormat indonesianDateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public SalesReportFrame() {
        setTitle("Laporan Penjualan");
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

        // Panel untuk section 1: Tanggal Mulai dan Tanggal Akhir
        JPanel dateRangePanel = new JPanel(new GridBagLayout());
        dateRangePanel.setBorder(BorderFactory.createTitledBorder("Rentang Tanggal"));
        dateRangePanel.setPreferredSize(new Dimension(400, 150));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Tanggal Mulai
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel startDateLabel = new JLabel("Tanggal Mulai:");
        dateRangePanel.add(startDateLabel, gbc);

        gbc.gridx = 1;
        UtilDateModel modelStart = new UtilDateModel();
        JDatePanelImpl datePanelStart = new JDatePanelImpl(modelStart);
        datePickerStart = new JDatePickerImpl(datePanelStart);
        dateRangePanel.add(datePickerStart, gbc);

        // Tanggal Akhir
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel endDateLabel = new JLabel("Tanggal Akhir:");
        dateRangePanel.add(endDateLabel, gbc);

        gbc.gridx = 1;
        UtilDateModel modelEnd = new UtilDateModel();
        JDatePanelImpl datePanelEnd = new JDatePanelImpl(modelEnd);
        datePickerEnd = new JDatePickerImpl(datePanelEnd);
        dateRangePanel.add(datePickerEnd, gbc);

        // Panel untuk section 2: Cari Nama Barang
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Cari Barang"));
        searchPanel.setPreferredSize(new Dimension(400, 150));

        // Cari Nama Barang
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel searchItemLabel = new JLabel("Nama Barang:");
        searchPanel.add(searchItemLabel, gbc);

        gbc.gridx = 1;
        List<String> productNames = getProductNames();
        productNames.add(0, "Semua");
        searchItemComboBox = new JComboBox<>(productNames.toArray(new String[0]));
        searchPanel.add(searchItemComboBox, gbc);

        // Panel untuk input (section 1 dan section 2)
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(dateRangePanel);
        inputPanel.add(searchPanel);

        // Panel judul dan tanggal
        JPanel titleDatePanel = new JPanel(new BorderLayout());

        JLabel tableTitleLabel = new JLabel("Laporan Penjualan");
        tableTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tableTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleDatePanel.add(tableTitleLabel, BorderLayout.NORTH);

        String currentDate = indonesianDateFormat.format(new Date());
        dateRangeLabel = new JLabel("Semua Penjualan");
        dateRangeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleDatePanel.add(dateRangeLabel, BorderLayout.CENTER);

        // Panel untuk tombol Generate dan Reset
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton generateButton = new JButton("Generate");
        JButton resetButton = new JButton("Reset");

        buttonPanel.add(generateButton);
        buttonPanel.add(resetButton);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setPreferredSize(new Dimension(1300, 250));
        topPanel.add(inputPanel);
        topPanel.add(buttonPanel);
        topPanel.add(titleDatePanel);

        contentPanel.add(topPanel, BorderLayout.NORTH);

        // Panel untuk tabel laporan penjualan
        JPanel tablePanel = new JPanel(new BorderLayout());

        String[] columnNames = {"Tanggal", "Kode Penjualan", "Nama Barang", "Jumlah", "Jenis Pelanggan", "Total"};
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
        setVisible(true);

        // Load data from database
        loadDataFromDatabase();

        // Action listener untuk tombol "Generate"
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTable();
            }
        });

        // Action listener untuk tombol "Reset"
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetFields();
            }
        });
    }

    private void updateTable() {
        String selectedItem = (String) searchItemComboBox.getSelectedItem();
        Date startDate = (Date) datePickerStart.getModel().getValue();
        Date endDate = (Date) datePickerEnd.getModel().getValue();

        if (startDate == null || endDate == null) {
            JOptionPane.showMessageDialog(this, "Harap pilih rentang tanggal yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update dateRangeLabel with selected start and end dates
        dateRangeLabel.setText("Tanggal " + indonesianDateFormat.format(startDate) + " s.d. " + indonesianDateFormat.format(endDate));

        StringBuilder queryBuilder = new StringBuilder("SELECT penjualan.tanggal, penjualan.kode, list_produk.nama AS nama_barang, detail_penjualan.jumlah, 'Cash' AS jenis_pelanggan, (detail_penjualan.jumlah * list_produk.harga_jual) AS total " +
                "FROM penjualan " +
                "JOIN detail_penjualan ON penjualan.id_penjualan = detail_penjualan.id_penjualan " +
                "JOIN list_produk ON detail_penjualan.id_produk = list_produk.id_list_produk WHERE 1=1");

        if (!"Semua".equals(selectedItem)) {
            queryBuilder.append(" AND list_produk.nama = '").append(selectedItem).append("'");
        }

        queryBuilder.append(" AND penjualan.tanggal BETWEEN '").append(dateFormat.format(startDate)).append("' AND '").append(dateFormat.format(endDate)).append("'");

        try (Connection connection = Dbconnect.getConnect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(queryBuilder.toString())) {

            tableModel.setRowCount(0); // Clear existing data
            while (resultSet.next()) {
                String tanggal = indonesianDateFormat.format(resultSet.getDate("tanggal"));
                String kode = resultSet.getString("kode");
                String namaBarang = resultSet.getString("nama_barang");
                int jumlah = resultSet.getInt("jumlah");
                String jenisPelanggan = resultSet.getString("jenis_pelanggan");
                double total = resultSet.getDouble("total");
                tableModel.addRow(new Object[]{tanggal, kode, namaBarang, jumlah, jenisPelanggan, "Rp" + total});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan pada database: " + e.getMessage());
        }
    }

    private void resetFields() {
        datePickerStart.getModel().setValue(null);
        datePickerEnd.getModel().setValue(null);
        searchItemComboBox.setSelectedIndex(0);
        loadDataFromDatabase();
        dateRangeLabel.setText("Semua Penjualan");
    }

    private void loadDataFromDatabase() {
        String query = "SELECT penjualan.tanggal, penjualan.kode, list_produk.nama AS nama_barang, detail_penjualan.jumlah, 'Cash' AS jenis_pelanggan, (detail_penjualan.jumlah * list_produk.harga_jual) AS total " +
                "FROM penjualan " +
                "JOIN detail_penjualan ON penjualan.id_penjualan = detail_penjualan.id_penjualan " +
                "JOIN list_produk ON detail_penjualan.id_produk = list_produk.id_list_produk";
        try (Connection connection = Dbconnect.getConnect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            tableModel.setRowCount(0); // Clear existing data
            while (resultSet.next()) {
                String tanggal = indonesianDateFormat.format(resultSet.getDate("tanggal"));
                String kode = resultSet.getString("kode");
                String namaBarang = resultSet.getString("nama_barang");
                int jumlah = resultSet.getInt("jumlah");
                String jenisPelanggan = resultSet.getString("jenis_pelanggan");
                double total = resultSet.getDouble("total");
                tableModel.addRow(new Object[]{tanggal, kode, namaBarang, jumlah, jenisPelanggan, "Rp" + total});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan pada database: " + e.getMessage());
        }
    }

    private List<String> getProductNames() {
        List<String> productNames = new ArrayList<>();
        try (Connection connection = Dbconnect.getConnect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT nama FROM list_produk")) {

            while (resultSet.next()) {
                productNames.add(resultSet.getString("nama"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan pada database: " + e.getMessage());
        }
        return productNames;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SalesReportFrame());
    }
}