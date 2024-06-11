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

public class SalesReportFrame extends JFrame {
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
        JPanel dateRangePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        dateRangePanel.setBorder(BorderFactory.createTitledBorder("Rentang Tanggal"));
        dateRangePanel.setPreferredSize(new Dimension(300, 100));

        JPanel startDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel startDateLabel = new JLabel("Tanggal Mulai:");
        JTextField startDateTextField = new JTextField(10);
        startDatePanel.add(startDateLabel);
        startDatePanel.add(startDateTextField);

        JPanel endDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel endDateLabel = new JLabel("Tanggal Akhir:");
        JTextField endDateTextField = new JTextField(10);
        endDatePanel.add(endDateLabel);
        endDatePanel.add(endDateTextField);

        dateRangePanel.add(startDatePanel);
        dateRangePanel.add(endDatePanel);

        // Panel untuk section 2: Cari Nama Barang
        JPanel searchPanel = new JPanel(new GridLayout(1, 1, 5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Cari Barang"));
        searchPanel.setPreferredSize(new Dimension(300, 50));

        JPanel searchItemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchItemLabel = new JLabel("Nama Barang:");
        JComboBox<String> searchItemComboBox = new JComboBox<>(getProductNames().toArray(new String[0]));
        searchItemPanel.add(searchItemLabel);
        searchItemPanel.add(searchItemComboBox);

        searchPanel.add(searchItemPanel);

        // Panel input (section 1 dan section 2)
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(dateRangePanel);
        inputPanel.add(searchPanel);

        // Panel judul dan tanggal
        JPanel titleDatePanel = new JPanel(new BorderLayout());

        JLabel tableTitleLabel = new JLabel("Laporan Penjualan");
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

        // Panel untuk tabel laporan penjualan
        JPanel tablePanel = new JPanel(new BorderLayout());

        String[] columnNames = {"Tanggal", "Kode Penjualan", "Nama Barang", "Jumlah", "Jenis Pelanggan", "Total"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
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
        loadDataFromDatabase(model);

        // Filter by product name when Generate button is clicked
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedProductName = (String) searchItemComboBox.getSelectedItem();
                filterDataByProductName(selectedProductName, model);
            }
        });
    }

    private void loadDataFromDatabase(DefaultTableModel model) {
        try (Connection connection = Dbconnect.getConnect()) {
            String query = "SELECT penjualan.tanggal, penjualan.kode, list_produk.nama AS nama_barang, detail_penjualan.jumlah, 'Cash' AS jenis_pelanggan, (detail_penjualan.jumlah * list_produk.harga_jual) AS total FROM penjualan LEFT JOIN detail_penjualan ON penjualan.id_penjualan = detail_penjualan.id_penjualan LEFT JOIN list_produk ON detail_penjualan.id_produk = list_produk.id_list_produk";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String tanggal = resultSet.getString("tanggal");
                String kodePenjualan = resultSet.getString("kode");
                String namaBarang = resultSet.getString("nama_barang");
                int jumlah = resultSet.getInt("jumlah");
                String jenisPelanggan = resultSet.getString("jenis_pelanggan");
                double total = resultSet.getDouble("total");
                // Add data to the table
                model.addRow(new Object[]{tanggal, kodePenjualan, namaBarang, jumlah, jenisPelanggan, "Rp" + total});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve data from the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
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

    private void filterDataByProductName(String productName, DefaultTableModel model) {
        model.setRowCount(0); // Clear the table before adding filtered data
        try (Connection connection = Dbconnect.getConnect()) {
            String query = "SELECT penjualan.tanggal, penjualan.kode, list_produk.nama AS nama_barang, detail_penjualan.jumlah, 'Cash' AS jenis_pelanggan, (detail_penjualan.jumlah * list_produk.harga_jual) AS total FROM penjualan LEFT JOIN detail_penjualan ON penjualan.id_penjualan = detail_penjualan.id_penjualan LEFT JOIN list_produk ON detail_penjualan.id_produk = list_produk.id_list_produk WHERE list_produk.nama = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, productName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String tanggal = resultSet.getString("tanggal");
                String kodePenjualan = resultSet.getString("kode");
                String namaBarang = resultSet.getString("nama_barang");
                int jumlah = resultSet.getInt("jumlah");
                String jenisPelanggan = resultSet.getString("jenis_pelanggan");
                double total = resultSet.getDouble("total");
                // Add data to the table
                model.addRow(new Object[]{tanggal, kodePenjualan, namaBarang, jumlah, jenisPelanggan, "Rp" + total});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve filtered data from the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new SalesReportFrame();
    }
}
